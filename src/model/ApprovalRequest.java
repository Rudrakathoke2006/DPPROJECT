package model;

public class ApprovalRequest {

    private String eventId;
    private String eventName;
    private String eventType;
    private String eventLevel;

    private String approverUsername;
    private String approverRole;

    private String status;
    private String remarks;

    public ApprovalRequest(
            String eventId,
            String eventName,
            String eventType,
            String eventLevel,
            String approverUsername,
            String approverRole,
            String status,
            String remarks) {

        this.eventId = eventId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventLevel = eventLevel;
        this.approverUsername = approverUsername;
        this.approverRole = approverRole;
        this.status = status;
        this.remarks = remarks;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventLevel() {
        return eventLevel;
    }

    public String getApproverUsername() {
        return approverUsername;
    }

    public String getApproverRole() {
        return approverRole;
    }

    public String getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setApproverUsername(String approverUsername) {
        this.approverUsername = approverUsername;
    }

    public void setApproverRole(String approverRole) {
        this.approverRole = approverRole;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}