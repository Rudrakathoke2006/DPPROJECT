package factory;

import model.Event;

public abstract class EventCreator {

    // FACTORY METHOD
    public abstract Event createEvent(
            String eventId,
            String eventName,
            String organizer,
            String date,
            String venue
    );

    // Common operation
    public Event prepareEvent(
            String eventId,
            String eventName,
            String organizer,
            String date,
            String venue) {

        Event event = createEvent(
                eventId,
                eventName,
                organizer,
                date,
                venue
        );

        System.out.println(
                "\nEvent created successfully!"
        );

        return event;
    }
}