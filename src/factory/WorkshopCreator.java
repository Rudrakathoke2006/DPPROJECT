package factory;

import model.Event;

public class WorkshopCreator extends EventCreator {

    @Override
    public Event createEvent(
            String eventId,
            String eventName,
            String organizer,
            String date,
            String venue) {

        return new Workshop(
                eventId,
                eventName,
                organizer,
                date,
                venue
        );
    }
}