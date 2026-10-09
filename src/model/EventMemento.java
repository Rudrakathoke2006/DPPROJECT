package model;

/** Immutable snapshot of editable event metadata only. Workflow state and registrations are excluded. */
public final class EventMemento {
    private final String eventId, eventName, date, venue;
    private final String description, budget, capacity, durationHours, equipment, specialInstructions;

    public EventMemento(String eventId, String eventName, String date, String venue,
                        String description, String budget, String capacity,
                        String durationHours, String equipment, String specialInstructions) {
        this.eventId = safe(eventId); this.eventName = safe(eventName);
        this.date = safe(date); this.venue = safe(venue);
        this.description = safe(description); this.budget = safe(budget);
        this.capacity = safe(capacity); this.durationHours = safe(durationHours);
        this.equipment = safe(equipment); this.specialInstructions = safe(specialInstructions);
    }
    private static String safe(String s) { return s == null ? "" : s; }
    public String getEventId(){return eventId;} public String getEventName(){return eventName;}
    public String getDate(){return date;} public String getVenue(){return venue;}
    public String getDescription(){return description;} public String getBudget(){return budget;}
    public String getCapacity(){return capacity;} public String getDurationHours(){return durationHours;}
    public String getEquipment(){return equipment;} public String getSpecialInstructions(){return specialInstructions;}
}
