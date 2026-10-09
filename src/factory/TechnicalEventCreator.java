package factory;

import model.Event;

public class TechnicalEventCreator extends EventCreator {

    @Override
    public Event createEvent(
            String eventId,
            String eventName,
            String organizer,
            String date,
            String venue) {

        return new TechnicalEvent(
                eventId,
                eventName,
                organizer,
                date,
                venue
        );
    }
}