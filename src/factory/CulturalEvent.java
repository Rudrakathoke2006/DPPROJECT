package factory;

import model.Event;

public class CulturalEvent extends Event {

    public CulturalEvent(String eventId,
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
                "CULTURAL"
        );
    }

    @Override
    public void conductEvent() {

        view.ConsoleView.println(
                "Conducting cultural event with "
                        + "stage and performance facilities."
        );
    }

    // PROTOTYPE: copy this event into a new, independent CulturalEvent
    @Override
    public Event copyEventTemplate(String newEventId,
                                   String newEventName,
                                   String organizer,
                                   String newDate,
                                   String newVenue) {

        CulturalEvent copy = new CulturalEvent(
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