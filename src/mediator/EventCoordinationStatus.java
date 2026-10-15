package mediator;

/** Read-only snapshot of one event's lifecycle, derived from the existing CSV files. */
public final class EventCoordinationStatus {
    public final String eventId, eventName, eventType, eventLevel, eventStatus;
    public final String approvalStage;     // e.g. "Awaiting HOD (hod_user)" / "Final: APPROVED" / "No approval row"
    public final String allocation;        // venue name or "NOT ALLOCATED"
    public final int registrations;
    public final int notifications;
    public final String workflowState;     // one-line summary
    public final String latestActivity;    // latest mediator log entry or "none"

    public EventCoordinationStatus(String eventId, String eventName, String eventType, String eventLevel,
                                   String eventStatus, String approvalStage, String allocation,
                                   int registrations, int notifications, String workflowState,
                                   String latestActivity) {
        this.eventId = eventId; this.eventName = eventName; this.eventType = eventType;
        this.eventLevel = eventLevel; this.eventStatus = eventStatus; this.approvalStage = approvalStage;
        this.allocation = allocation; this.registrations = registrations; this.notifications = notifications;
        this.workflowState = workflowState; this.latestActivity = latestActivity;
    }

    public void display() {
        view.ConsoleView.println("\nEvent ID        : " + eventId + " (" + eventName + ")");
        view.ConsoleView.println("Type / Level    : " + eventType + " / " + eventLevel);
        view.ConsoleView.println("Event status    : " + eventStatus);
        view.ConsoleView.println("Approval        : " + approvalStage);
        view.ConsoleView.println("Allocation      : " + allocation);
        view.ConsoleView.println("Registrations   : " + registrations);
        view.ConsoleView.println("Notifications   : " + notifications);
        view.ConsoleView.println("Workflow state  : " + workflowState);
        view.ConsoleView.println("Latest activity : " + latestActivity);
    }
}
