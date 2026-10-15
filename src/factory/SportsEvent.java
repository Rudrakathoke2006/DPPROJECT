package factory;

import model.Event;

public class SportsEvent extends Event {

    public SportsEvent(String eventId,
                       String eventName,
                       String organizer,
                       String date,
                       String venue) {

        super(
                eventId,
                eventName,
                organizer,
                date,
                venue,
                "SPORTS"
        );
    }

    @Override
    public void conductEvent() {

        view.ConsoleView.println(
                "Conducting sports event with "
                        + "sports equipment and facilities."
        );
    }

    // PROTOTYPE: copy this event into a new, independent SportsEvent
    @Override
    public Event copyEventTemplate(String newEventId,
                                   String newEventName,
                                   String organizer,
                                   String newDate,
                                   String newVenue) {

        SportsEvent copy = new SportsEvent(
                newEventId,
                newEventName,
                organizer,
                newDate,
                newVenue
        );

        copyTemplateInto(copy);
        return copy;
    }
}