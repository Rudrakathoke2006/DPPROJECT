package mediator;

import filehandling.ApprovalFileHandler;
import filehandling.UserFileHandler;
import model.ApprovalRequest;

/** Adapter over the existing approval storage. Chain of Responsibility logic stays in chain/. */
final class ApprovalModule {
    private final ApprovalFileHandler handler = new ApprovalFileHandler();

    boolean exists(String eventId) { return handler.approvalExists(eventId); }

    String[] find(String eventId) {
        for (String[] r : CsvRows.read("data/approvals.csv", 8)) if (r[0].equalsIgnoreCase(eventId)) return r;
        return null;
    }

    /** Creates the single initial Faculty request from an events.csv row. */
    boolean createInitialRequest(String[] eventRow) {
        String faculty = new UserFileHandler().findUsernameByRole("FACULTY");
        if (faculty == null || faculty.trim().isEmpty()) {
            view.ConsoleView.println("No Faculty approver is configured.");
            return false;
        }
        return handler.saveApproval(new ApprovalRequest(eventRow[0], eventRow[1], eventRow[2], eventRow[3],
                faculty, "FACULTY", "PENDING", "Awaiting Faculty approval."));
    }

    /** Human-readable stage derived from approvals.csv. */
    String stage(String[] approval) {
        if (approval == null) return "No approval request found";
        String st = approval[6].trim(), role = approval[5].trim();
        if (st.equalsIgnoreCase("PENDING")) return "Awaiting " + role + " (" + approval[4] + ")";
        return "Final decision: " + st + " by " + role;
    }
}
