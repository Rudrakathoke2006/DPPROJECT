package filehandling;

import model.ApprovalRequest;
import model.Event;

import java.io.*;

public class ApprovalFileHandler {

    private static final String FILE_PATH = "data/approvals.csv";
    private static final String HEADER = "eventId,eventName,eventType,eventLevel,approverUsername,approverRole,status,remarks";

    public boolean saveApproval(ApprovalRequest request) {
        ensureFile();
        if (approvalExists(request.getEventId())) {
            view.ConsoleView.println("Approval request already exists for this event.");
            return false;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(toCsv(request));
            view.ConsoleView.println("Approval record saved.");
            return true;
        } catch (IOException e) {
            view.ConsoleView.println("Error saving approval: " + e.getMessage());
            return false;
        }
    }

    public boolean createInitialApproval(Event event, String facultyUsername) {
        if (facultyUsername == null || facultyUsername.trim().isEmpty()) {
            view.ConsoleView.println("No Faculty approver is configured.");
            return false;
        }
        ApprovalRequest request = new ApprovalRequest(
                event.getEventId(), event.getEventName(), event.getEventType(), event.getEventLevel(),
                facultyUsername, "FACULTY", "PENDING", "Awaiting Faculty approval."
        );
        return saveApproval(request);
    }

    public boolean approvalExists(String eventId) {
        ensureFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 8 && data[0].equalsIgnoreCase(eventId)) return true;
            }
        } catch (IOException e) {
            view.ConsoleView.println("Error reading approvals: " + e.getMessage());
        }
        return false;
    }

    private void ensureFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) return;
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(HEADER);
        } catch (IOException e) {
            view.ConsoleView.println("Error creating approvals file: " + e.getMessage());
        }
    }

    private String toCsv(ApprovalRequest r) {
        return clean(r.getEventId()) + "," + clean(r.getEventName()) + "," + clean(r.getEventType()) + "," + clean(r.getEventLevel()) + ","
                + clean(r.getApproverUsername()) + "," + clean(r.getApproverRole()) + ","
                + clean(r.getStatus()) + "," + clean(r.getRemarks());
    }

    private String clean(String value) {
        return value == null ? "" : value.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }
}
