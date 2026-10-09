package model;

public abstract class Event implements EventPrototype {

    private String eventId;
    private String eventName;
    private String organizer;
    private String date;
    private String venue;
    private String eventType;
    private String eventLevel;
    private String status;
    private EventSpecification specification;

    public Event(String eventId,
                 String eventName,
                 String organizer,
                 String date,
                 String venue,
                 String eventType) {

        this.eventId = eventId;
        this.eventName = eventName;
        this.organizer = organizer;
        this.date = date;
        this.venue = venue;
        this.eventType = eventType;
        this.eventLevel = "DEPARTMENT";
        this.status = "PENDING";
        this.specification = EventSpecification.defaults();
    }

    public String getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public String getOrganizer() { return organizer; }
    public String getDate() { return date; }
    public String getVenue() { return venue; }
    public String getEventType() { return eventType; }
    public String getEventLevel() { return eventLevel; }
    public void setEventLevel(String eventLevel) { this.eventLevel = eventLevel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public EventSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(EventSpecification specification) {
        this.specification = (specification == null)
                ? EventSpecification.defaults()
                : specification;
    }

    /**
     * Helper for the Prototype copy methods in the concrete subclasses.
     * Copies only template data (level + optional details).
     * Status is NOT copied: the new event starts as PENDING from the constructor.
     */
    protected void copyTemplateInto(Event target) {
        target.setEventLevel(this.eventLevel);
        target.setSpecification(this.specification.copyAsTemplate());
    }

    public abstract void conductEvent();

    @Override
    public abstract Event copyEventTemplate(String newEventId,
                                            String newEventName,
                                            String organizer,
                                            String newDate,
                                            String newVenue);

    public void displayDetails() {

        System.out.println("\n-----------------------------");
        System.out.println("Event ID   : " + eventId);
        System.out.println("Event Name : " + eventName);
        System.out.println("Type       : " + eventType);
        System.out.println("Level      : " + eventLevel);
        System.out.println("Organizer  : " + organizer);
        System.out.println("Date       : " + date);
        System.out.println("Venue      : " + venue);
        System.out.println("Status     : " + status);
        System.out.println("-----------------------------");
    }
}