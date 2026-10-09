package factory;

import model.Event;

public class CulturalEventCreator extends EventCreator {

    @Override
    public Event createEvent(
            String eventId,
            String eventName,
            String organizer,
            String date,
            String venue) {

        return new CulturalEvent(
                eventId,
                eventName,
                organizer,
                date,
                venue
        );
    }
}