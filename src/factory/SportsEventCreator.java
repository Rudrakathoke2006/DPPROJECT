package factory;

import model.Event;

public class SportsEventCreator extends EventCreator {

    @Override
    public Event createEvent(
            String eventId,
            String eventName,
            String organizer,
            String date,
            String venue) {

        return new SportsEvent(
                eventId,
                eventName,
                organizer,
                date,
                venue
        );
    }
}