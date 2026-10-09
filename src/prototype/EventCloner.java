package prototype;

import factory.EventCreator;
import factory.EventCreatorProvider;
import filehandling.EventDetailsFileHandler;
import filehandling.EventFileHandler;
import model.Event;

/**
 * Loads a stored event as a concrete Event object (using the existing Factory Method)
 * and clones it through the PROTOTYPE contract (Event.copyEventTemplate).
 */
public class EventCloner {

    private final EventFileHandler eventFileHandler = new EventFileHandler();
    private final EventDetailsFileHandler detailsFileHandler = new EventDetailsFileHandler();

    /** Rebuilds the stored event, or returns null if missing / unsupported type. */
    public Event loadEvent(String eventId) {
        String[] row = eventFileHandler.findEventById(eventId);
        if (row == null) return null;

        EventCreator creator = EventCreatorProvider.forType(row[2]);
        if (creator == null) return null;

        // row: id, name, type, level, organizer, date, venue, status
        Event event = creator.createEvent(row[0], row[1], row[4], row[5], row[6]);
        event.setEventLevel(row[3]);
        event.setStatus(row[7]);
        event.setSpecification(detailsFileHandler.findDetails(row[0]));
        return event;
    }

    /** Polymorphic copy: the concrete subclass decides what object is created. */
    public Event cloneEvent(Event source, String newEventId, String newEventName,
                            String organizer, String newDate, String newVenue) {
        return source.copyEventTemplate(newEventId, newEventName, organizer, newDate, newVenue);
    }
}