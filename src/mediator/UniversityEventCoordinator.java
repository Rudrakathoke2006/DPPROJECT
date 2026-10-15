package mediator;

import filehandling.EventFileHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * MEDIATOR (concrete). Thin: it validates each signal against the CSV state, delegates to the existing
 * services through the module adapters, and logs. Modules never call each other or the mediator, so the
 * flow is unidirectional (no update -> notification -> update loops). Chain of Responsibility, Observer,
 * Bridge, Abstract Factory and Strategy keep their own responsibilities.
 */
public class UniversityEventCoordinator implements EventCoordinator {
    private final EventFileHandler events = new EventFileHandler();
    private final ApprovalModule approvals = new ApprovalModule();
    private final ResourceModule resources = new ResourceModule();
    private final RegistrationModule registrations = new RegistrationModule();
    private final NotificationModule notifications = new NotificationModule();
    private final CoordinationLog log = new CoordinationLog();

    @Override
    public boolean eventCreated(String eventId) {
        String[] ev = events.findEventById(eventId);
        if (ev == null) return false;
        if (!"PENDING".equalsIgnoreCase(ev[7])) {
            log.appendIfChanged(eventId, ev[2], "eventCreated", "IGNORED", "event status is " + ev[7] + ", not PENDING");
            return false;
        }
        boolean already = approvals.exists(eventId);
        boolean routed = already || approvals.createInitialRequest(ev);   // never a second approval row
        log.appendIfChanged(eventId, ev[2], "eventCreated",
                routed ? "APPROVAL_ROUTED" : "APPROVAL_SETUP_FAILED",   // same label whether created now or earlier
                "level " + ev[3] + " starts at Faculty");
        return routed;
    }

    @Override
    public void approvalFinalized(String eventId, String outcome, String authorityRole) {
        String[] ev = events.findEventById(eventId);
        if (ev == null) return;
        if (outcome == null || !ev[7].equalsIgnoreCase(outcome)) {
            log.appendIfChanged(eventId, ev[2], "approvalFinalized", "IGNORED",
                    "signal " + outcome + " does not match events.csv status " + ev[7]);
            return;
        }
        if ("APPROVED".equalsIgnoreCase(outcome)) {
            String next = resources.allocation(eventId) != null
                    ? "resources already allocated" : "eligible for resource allocation";
            log.appendIfChanged(eventId, ev[2], "approvalFinalized", "APPROVED",
                    "final authority " + authorityRole + "; " + next);
        } else {
            log.appendIfChanged(eventId, ev[2], "approvalFinalized", "REJECTED",
                    "closed by " + authorityRole + "; no allocation, registration or announcements");
        }
    }

    @Override
    public void resourcesAllocated(String eventId, String venueName) {
        String[] ev = events.findEventById(eventId);
        if (ev == null) return;
        String[] alloc = resources.allocation(eventId);
        if (!"APPROVED".equalsIgnoreCase(ev[7]) || alloc == null) {
            log.appendIfChanged(eventId, ev[2], "resourcesAllocated", "IGNORED",
                    "event is " + ev[7] + (alloc == null ? " and no resources row exists" : ""));
            return;
        }
        log.appendIfChanged(eventId, ev[2], "resourcesAllocated", "ALLOCATED",
                "venue " + alloc[3] + " (proposed venue " + ev[6] + " unchanged)");
    }

    @Override
    public void registrationCompleted(String eventId, String studentUsername) {
        String[] ev = events.findEventById(eventId);
        if (ev == null) return;
        if (!registrations.isRegistered(eventId, studentUsername)) {
            log.appendIfChanged(eventId, ev[2], "registrationCompleted", "IGNORED",
                    studentUsername + " has no registration row");
            return;
        }
        log.append(eventId, ev[2], "registrationCompleted", "REGISTERED",
                studentUsername + "; total registrations " + registrations.count(eventId));
    }

    @Override
    public int eventUpdated(String eventId, String message) {
        String[] ev = events.findEventById(eventId);
        if (ev == null || !"APPROVED".equalsIgnoreCase(ev[7])) return 0;   // updates only for approved events
        int count = notifications.notifyRegistrants(ev[0], ev[1], message);  // existing Observer + Bridge
        log.append(eventId, ev[2], "eventUpdated", count > 0 ? "NOTIFIED" : "NO_RECIPIENTS", count + " registered student(s)");
        return count;
    }

    @Override
    public void eventEdited(String eventId, String action) {
        String[] ev = events.findEventById(eventId);
        if (ev == null) return;
        log.append(eventId, ev[2], "eventEdited", "REAPPROVAL_REQUIRED",
                action + "; status " + ev[7] + " and routed back through Faculty");
    }

    @Override
    public EventCoordinationStatus statusOf(String eventId) {
        String[] ev = events.findEventById(eventId);
        return ev == null ? null : build(ev);
    }

    @Override
    public List<EventCoordinationStatus> allStatuses(boolean approvedOnly) {
        List<EventCoordinationStatus> list = new ArrayList<>();
        for (String[] ev : CsvRows.read("data/events.csv", 8)) {
            if (approvedOnly && !"APPROVED".equalsIgnoreCase(ev[7])) continue;
            list.add(build(ev));
        }
        return list;
    }

    private EventCoordinationStatus build(String[] ev) {
        String id = ev[0];
        String[] approval = approvals.find(id);
        String[] alloc = resources.allocation(id);
        int regs = registrations.count(id);
        int notes = notifications.historyCount(id);
        String[] last = log.latest(id);
        String latest = last == null ? "none recorded"
                : last[5] + " | " + last[2] + " -> " + last[3] + " (" + last[4] + ")";
        String state;
        if ("REJECTED".equalsIgnoreCase(ev[7])) state = "CLOSED - rejected";
        else if ("PENDING".equalsIgnoreCase(ev[7])) state = "IN APPROVAL - " + approvals.stage(approval);
        else if (alloc == null) state = "APPROVED - eligible for resource allocation";
        else state = regs > 0 ? "ALLOCATED - open with " + regs + " registration(s)" : "ALLOCATED - open for registration";
        return new EventCoordinationStatus(id, ev[1], ev[2], ev[3], ev[7], approvals.stage(approval),
                alloc == null ? "NOT ALLOCATED" : alloc[3], regs, notes, state, latest);
    }
}
