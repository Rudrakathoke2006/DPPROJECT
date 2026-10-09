package builder;

import factory.EventCreator;
import model.Event;
import model.EventSpecification;

/**
 * BUILDER: assembles a complex Event step by step.
 *  - mandatory fields are validated in build()
 *  - optional fields get defaults when skipped
 *  - the concrete Event is created by the EXISTING Factory Method (EventCreator)
 */
public class EventBuilder {

    private final EventCreator creator;

    // mandatory
    private String eventId;
    private String eventName;
    private String organizer;
    private String date;
    private String venue;
    private String level;

    // optional (defaults mean "not provided")
    private String description;
    private double budget = 0;
    private int capacity = 0;
    private int durationHours = 0;
    private String equipment;
    private String specialInstructions;

    public EventBuilder(EventCreator creator) {
        this.creator = creator;
    }

    public EventBuilder withEventId(String eventId) { this.eventId = eventId; return this; }
    public EventBuilder withName(String eventName) { this.eventName = eventName; return this; }
    public EventBuilder withOrganizer(String organizer) { this.organizer = organizer; return this; }
    public EventBuilder withDate(String date) { this.date = date; return this; }
    public EventBuilder withVenue(String venue) { this.venue = venue; return this; }
    public EventBuilder withLevel(String level) { this.level = level; return this; }

    public EventBuilder withDescription(String description) { this.description = description; return this; }
    public EventBuilder withBudget(double budget) { this.budget = budget; return this; }
    public EventBuilder withCapacity(int capacity) { this.capacity = capacity; return this; }
    public EventBuilder withDuration(int durationHours) { this.durationHours = durationHours; return this; }
    public EventBuilder withEquipment(String equipment) { this.equipment = equipment; return this; }
    public EventBuilder withSpecialInstructions(String text) { this.specialInstructions = text; return this; }

    /**
     * Validates everything first, then creates the Event.
     * Nothing is written to any file here, so a failed build leaves no partial data.
     *
     * @throws IllegalArgumentException with a readable message if any field is invalid
     */
    public Event build() {
        if (creator == null) {
            throw new IllegalArgumentException("Event type is required.");
        }

        EventValidator.validateCore(eventId, eventName, organizer, date, venue, level);

        // constructor validates budget / capacity / duration and applies defaults
        EventSpecification specification = new EventSpecification(
                description, budget, capacity, durationHours,
                equipment, specialInstructions);

        // EXISTING Factory Method creates the correct concrete event
        Event event = creator.createEvent(
                eventId.trim(), eventName.trim(), organizer.trim(),
                date.trim(), venue.trim());

        event.setEventLevel(level);
        event.setSpecification(specification);
        return event;
    }
}
