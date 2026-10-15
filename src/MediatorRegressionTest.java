import mediator.*;
import model.*;
import singleton.FestManagementSystem;
import controller.EventWorkflowController;
import java.nio.file.*;
import java.util.*;

/** Run only from an isolated scratch copy. Modifies data CSV files. */
public class MediatorRegressionTest {
    static int checks = 0;
    static void ok(boolean b, String msg) { checks++; if (!b) throw new AssertionError(msg); }
    static List<String> lines(String f) throws Exception { return Files.readAllLines(Paths.get("data/" + f)); }
    static long count(String f, String prefix, String... contains) throws Exception {
        long n = 0;
        for (String s : lines(f).subList(1, lines(f).size())) {
            if (!s.startsWith(prefix)) continue;
            boolean all = true;
            for (String c : contains) if (!s.contains(c)) all = false;
            if (all) n++;
        }
        return n;
    }

    public static void main(String[] a) throws Exception {
        FestManagementSystem sys = FestManagementSystem.getInstance();
        EventCoordinator med = sys.getEventCoordinator();
        Faculty rahul = new Faculty("F101", "Dr Rahul Mehta", "rahul", "x");
        Student aarav = new Student("S101", "Aarav", "aarav", "x");
        Student meera = new Student("S102", "Meera", "meera", "x");

        // 1. creation -> exactly one approval request, one log row; replay is idempotent
        EventWorkflowController.createEvent(sys, rahul, new Scanner("1\nM901\nMed Tech\n12-12-2026\nLab\n1\n"));
        EventWorkflowController.createEvent(sys, rahul, new Scanner("2\nM902\nMed Fest\n13-12-2026\nHall\n3\n"));
        EventWorkflowController.createEvent(sys, rahul, new Scanner("3\nM903\nMed Cup\n14-12-2026\nField\n1\n"));
        ok(count("approvals.csv", "M901,") == 1 && count("approvals.csv", "M902,") == 1, "one approval row per new event");
        ok(count("coordination_log.csv", "M901,", "eventCreated", "APPROVAL_ROUTED") == 1, "creation logged once");
        ok(med.eventCreated("M901"), "replay still reports routed");
        ok(count("approvals.csv", "M901,") == 1, "replay creates no second approval");
        ok(count("coordination_log.csv", "M901,", "eventCreated") == 1, "replay does not duplicate log row");

        // 2. PENDING events: no allocation eligibility, no notifications, hidden from students
        ok(med.eventUpdated("M901", "x") == 0, "pending event cannot publish update");
        ok(count("notifications.csv", "M901,") == 0, "no notifications for pending event");
        ok(med.statusOf("M901").workflowState.startsWith("IN APPROVAL"), "pending state derived");
        for (EventCoordinationStatus s : med.allStatuses(true)) ok(!s.eventId.startsWith("M90"), "students never see pending events");

        // 3. Faculty decisions: M901 final approve, M902 forward only, M903 reject
        sys.getApprovalManager().showPendingApprovals("rahul", "FACULTY", new Scanner("yes\nyes\nno\n"));
        ok(count("coordination_log.csv", "M901,", "approvalFinalized", "APPROVED", "FACULTY") == 1, "department final approval signalled");
        ok(count("coordination_log.csv", "M902,", "approvalFinalized") == 0, "HOD forwarding is NOT a final-approval signal");
        ok(count("coordination_log.csv", "M903,", "approvalFinalized", "REJECTED") == 1, "rejection signalled");
        sys.getApprovalManager().showPendingApprovals("priya", "HOD", new Scanner("yes\n"));
        ok(count("coordination_log.csv", "M902,", "approvalFinalized") == 0, "Dean forwarding is NOT a final-approval signal");
        sys.getApprovalManager().showPendingApprovals("anjali", "DEAN", new Scanner("yes\n"));
        ok(count("coordination_log.csv", "M902,", "approvalFinalized", "APPROVED", "DEAN") == 1, "university final approval by Dean");

        // 4. mismatched/replayed signals are ignored or guarded
        med.approvalFinalized("M901", "APPROVED", "FACULTY");
        ok(count("coordination_log.csv", "M901,", "approvalFinalized") == 1, "replayed approval signal not duplicated");
        med.approvalFinalized("M903", "APPROVED", "FACULTY");
        ok(count("coordination_log.csv", "M903,", "approvalFinalized", "IGNORED") == 1, "signal contradicting events.csv is ignored");
        ok(med.statusOf("M903").workflowState.startsWith("CLOSED"), "rejected event closed");

        // 5. allocation is only for approved events and happens once
        EventWorkflowController.allocateResources(sys, new Scanner("M903\n"));
        ok(count("resources.csv", "M903,") == 0, "rejected event gets no allocation");
        EventWorkflowController.allocateResources(sys, new Scanner("M901\n"));
        ok(count("resources.csv", "M901,") == 1, "approved event allocated");
        ok(count("coordination_log.csv", "M901,", "resourcesAllocated", "ALLOCATED") == 1, "allocation logged once");
        EventWorkflowController.allocateResources(sys, new Scanner("M901\n"));
        ok(count("resources.csv", "M901,") == 1 && count("coordination_log.csv", "M901,", "resourcesAllocated") == 1, "repeat allocation guarded");
        ok(med.statusOf("M901").workflowState.startsWith("ALLOCATED"), "allocated state derived");
        ok(med.statusOf("M902").workflowState.startsWith("APPROVED - eligible"), "approved but unallocated is eligible");

        // 6. registration (existing flow) + notification only to registered students
        EventWorkflowController.registerForEvent(sys, aarav, new Scanner("M901\nEMAIL\n"));
        EventWorkflowController.registerForEvent(sys, aarav, new Scanner("M901\nEMAIL\n"));
        ok(count("registrations.csv", "R") == 1, "duplicate registration blocked by existing flow");
        ok(count("coordination_log.csv", "M901,", "registrationCompleted") == 1, "only the successful registration signalled");
        EventWorkflowController.registerForEvent(sys, meera, new Scanner("M901\nSMS\n"));
        EventWorkflowController.publishUpdate(sys, new Scanner("M901\nBring ID\n"));
        ok(count("notifications.csv", "M901,") == 2, "exactly one notification per registrant");
        ok(count("coordination_log.csv", "M901,", "eventUpdated", "NOTIFIED") == 1, "update logged");
        EventWorkflowController.publishUpdate(sys, new Scanner("M902\nNo one yet\n"));
        ok(count("notifications.csv", "M902,") == 0, "no registrants -> no notifications");
        EventWorkflowController.publishUpdate(sys, new Scanner("M903\nRejected\n"));
        ok(count("notifications.csv", "M903,") == 0, "rejected event cannot publish updates");

        // 7. status view
        EventCoordinationStatus s = med.statusOf("M901");
        ok(s.registrations == 2 && s.notifications == 2 && !s.allocation.equals("NOT ALLOCATED"), "status aggregates registrations/notifications/allocation");
        ok(med.statusOf("NOPE") == null, "unknown event -> null");
        EventWorkflowController.showCoordinationStatus(sys, aarav, new Scanner("M903\n"));
        EventWorkflowController.showCoordinationStatus(sys, rahul, new Scanner("\n"));
        // 8. approval rows still exactly one per event after the whole run
        ok(count("approvals.csv", "M901,") == 1 && count("approvals.csv", "M902,") == 1 && count("approvals.csv", "M903,") == 1, "no duplicate approvals after full lifecycle");
        System.out.println("MEDIATOR REGRESSION PASSED: " + checks + " assertions");
    }
}
