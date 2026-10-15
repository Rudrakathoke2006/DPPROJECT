package chain;

import filehandling.EventFileHandler;
import filehandling.UserFileHandler;
import mediator.EventCoordinator;
import mediator.UniversityEventCoordinator;
import model.ApprovalRequest;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class ApprovalManager {
    private static final String FILE_PATH = "data/approvals.csv";
    private static final String HEADER = "eventId,eventName,eventType,eventLevel,approverUsername,approverRole,status,remarks";
    private final ApprovalHandler approvalChain;
    private final UserFileHandler userFileHandler;
    private final EventFileHandler eventFileHandler;
    private final EventCoordinator coordinator;   // Mediator: told only about FINAL decisions

    public ApprovalManager() {
        this(new UniversityEventCoordinator());
    }

    public ApprovalManager(EventCoordinator coordinator) {
        this.coordinator = coordinator;
        ApprovalHandler faculty = new FacultyApprovalHandler();
        ApprovalHandler hod = new HODApprovalHandler();
        ApprovalHandler dean = new DeanApprovalHandler();

        faculty.setNextHandler(hod);
        hod.setNextHandler(dean);

        approvalChain = faculty;
        userFileHandler = new UserFileHandler();
        eventFileHandler = new EventFileHandler();
    }

    public void showPendingApprovals(String username, String role, Scanner scanner) {
        boolean foundAny = false;

        while (true) {
            // IMPORTANT: this method completely closes approvals.csv
            // before we try to update/replace it.
            ApprovalRequest request = findNextPendingApproval(username, role);

            if (request == null) {
                break;
            }

            foundAny = true;

            // Demonstrates the Chain of Responsibility handler flow.
            approvalChain.processRequest(request, username);

            boolean validDecision = processDecision(request, scanner);

            // If the user enters something other than yes/no,
            // leave the request pending and return to the menu.
            if (!validDecision) {
                break;
            }
        }

        if (!foundAny) {
            view.ConsoleView.println("\nNo pending approvals for you.");
        }
    }

    private ApprovalRequest findNextPendingApproval(String username, String role) {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            view.ConsoleView.println("Approvals file not found: " + FILE_PATH);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length < 8) {
                    continue;
                }

                boolean sameUser = data[4].equalsIgnoreCase(username);
                boolean sameRole = data[5].equalsIgnoreCase(role);
                boolean pending = data[6].equalsIgnoreCase("PENDING");

                if (sameUser && sameRole && pending) {
                    return new ApprovalRequest(
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            data[4],
                            data[5],
                            data[6],
                            data[7]
                    );
                }
            }
        } catch (IOException e) {
            view.ConsoleView.println("Error reading approvals: " + e.getMessage());
        }

        return null;
    }

    private boolean processDecision(ApprovalRequest request, Scanner scanner) {
        view.ConsoleView.print("\nApprove this event? (yes/no): ");
        String decision = view.ConsoleView.readLine(scanner).trim();

        if (decision.equalsIgnoreCase("yes")) {
            approve(request);
            return true;
        }

        if (decision.equalsIgnoreCase("no")) {
            reject(request);
            return true;
        }

        view.ConsoleView.println("Invalid choice. Request remains pending.");
        return false;
    }

    private void approve(ApprovalRequest request) {
        String role = request.getApproverRole().toUpperCase();
        String level = request.getEventLevel().toUpperCase();

        if (role.equals("FACULTY")) {
            if (level.equals("DEPARTMENT")) {
                finalizeApproval(request,
                        "Final approval granted by Faculty for Department-level event.");
            } else {
                forward(request, "HOD",
                        "Approved by Faculty; forwarded to HOD.");
            }
        } else if (role.equals("HOD")) {
            if (level.equals("INTER_DEPARTMENT")) {
                finalizeApproval(request,
                        "Final approval granted by HOD for Inter-Department-level event.");
            } else if (level.equals("UNIVERSITY")) {
                forward(request, "DEAN",
                        "Approved by HOD; forwarded to Dean.");
            }
        } else if (role.equals("DEAN") && level.equals("UNIVERSITY")) {
            finalizeApproval(request,
                    "Final approval granted by Dean for University-level event.");
        }
    }

    private void forward(ApprovalRequest request, String nextRole, String remarks) {
        String nextUsername = userFileHandler.findUsernameByRole(nextRole);

        if (nextUsername == null) {
            view.ConsoleView.println("No " + nextRole + " approver configured.");
            return;
        }

        request.setApproverUsername(nextUsername);
        request.setApproverRole(nextRole);
        request.setStatus("PENDING");
        request.setRemarks(remarks);

        if (updateApproval(request)) {
            view.ConsoleView.println("\n" + remarks);
        }
    }

    private void finalizeApproval(ApprovalRequest request, String remarks) {
        request.setStatus("APPROVED");
        request.setRemarks(remarks);

        if (updateApproval(request)) {
            boolean eventUpdated = eventFileHandler.updateEventStatus(
                    request.getEventId(), "APPROVED");

            if (eventUpdated) {
                view.ConsoleView.println("\n" + remarks
                        + " Event status is now APPROVED.");
                coordinator.approvalFinalized(request.getEventId(), "APPROVED", request.getApproverRole());
            } else {
                view.ConsoleView.println("\nApproval updated, but event status could not be updated.");
            }
        }
    }

    private void reject(ApprovalRequest request) {
        String rejectingRole = request.getApproverRole();

        request.setStatus("REJECTED");
        request.setRemarks("Rejected by " + rejectingRole + ".");

        if (updateApproval(request)) {
            boolean eventUpdated = eventFileHandler.updateEventStatus(
                    request.getEventId(), "REJECTED");

            if (eventUpdated) {
                view.ConsoleView.println("\nEvent rejected by " + rejectingRole + ".");
                coordinator.approvalFinalized(request.getEventId(), "REJECTED", rejectingRole);
            } else {
                view.ConsoleView.println("\nApproval rejected, but event status could not be updated.");
            }
        }
    }

    private boolean updateApproval(ApprovalRequest updatedRequest) {
        File original = new File(FILE_PATH);
        File temp = new File("data/approvals_temp.csv");
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(original));
             PrintWriter writer = new PrintWriter(new FileWriter(temp))) {

            writer.println(HEADER);
            reader.readLine(); // skip original header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length >= 8
                        && data[0].equalsIgnoreCase(updatedRequest.getEventId())) {
                    writer.println(toCsv(updatedRequest));
                    found = true;
                } else if (data.length >= 8) {
                    writer.println(line);
                }
            }

        } catch (IOException e) {
            view.ConsoleView.println("Error updating approval: " + e.getMessage());
            deleteTempFile(temp);
            return false;
        }

        if (!found) {
            view.ConsoleView.println("Approval record not found for event: "
                    + updatedRequest.getEventId());
            deleteTempFile(temp);
            return false;
        }

        try {
            Files.move(
                    temp.toPath(),
                    original.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
            return true;
        } catch (IOException e) {
            view.ConsoleView.println("Could not replace approvals file: " + e.getMessage());
            deleteTempFile(temp);
            return false;
        }
    }

    private void deleteTempFile(File temp) {
        if (temp.exists() && !temp.delete()) {
            view.ConsoleView.println("Warning: temporary approval file could not be deleted.");
        }
    }

    private String toCsv(ApprovalRequest r) {
        return clean(r.getEventId()) + ","
                + clean(r.getEventName()) + ","
                + clean(r.getEventType()) + ","
                + clean(r.getEventLevel()) + ","
                + clean(r.getApproverUsername()) + ","
                + clean(r.getApproverRole()) + ","
                + clean(r.getStatus()) + ","
                + clean(r.getRemarks());
    }

    private String clean(String value) {
        return value == null
                ? ""
                : value.replace(",", " ")
                       .replace("\n", " ")
                       .replace("\r", " ");
    }
}
