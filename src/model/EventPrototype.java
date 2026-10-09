package model;

/**
 * PROTOTYPE contract: an event can produce a new, independent copy of itself.
 * Each concrete event class implements it, so the copy always has the
 * correct concrete type (TechnicalEvent, CulturalEvent, ...).
 */
public interface EventPrototype {

    Event copyEventTemplate(String newEventId,
            String newEventName,
            String organizer,
            String newDate,
            String newVenue);
}