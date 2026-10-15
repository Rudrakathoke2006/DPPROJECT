package mediator;

import java.util.List;

/**
 * MEDIATOR (interface). Senders (controllers, ApprovalManager, creation services) talk ONLY to this
 * contract; they never call each other's modules directly. The concrete mediator decides which
 * existing services react. Signals: eventCreated, approvalFinalized, resourcesAllocated,
 * registrationCompleted, eventUpdated (+ eventEdited for Memento edits).
 */
public interface EventCoordinator {

    /** A PENDING event was saved. Ensures exactly one approval request exists. @return true if routed. */
    boolean eventCreated(String eventId);

    /** Final decision only (outcome APPROVED or REJECTED). Intermediate forwarding must NOT call this. */
    void approvalFinalized(String eventId, String outcome, String authorityRole);

    /** A final resource allocation row has been written for the event. */
    void resourcesAllocated(String eventId, String venueName);

    /** A student registration row has been written. */
    void registrationCompleted(String eventId, String studentUsername);

    /** Publish an update through the existing Observer/Bridge flow. @return number of students notified. */
    int eventUpdated(String eventId, String message);

    /** Memento edit/undo happened; the event went back through the approval chain. */
    void eventEdited(String eventId, String action);

    /** Derived coordination status of one event, or null if the event does not exist. */
    EventCoordinationStatus statusOf(String eventId);

    /** Statuses of all events (students: approved events only). */
    List<EventCoordinationStatus> allStatuses(boolean approvedOnly);
}
