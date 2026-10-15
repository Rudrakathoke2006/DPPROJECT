package factory;

import model.Event;

public class Workshop extends Event {

    public Workshop(String eventId,
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
                "WORKSHOP"
        );
    }

    @Override
    public void conductEvent() {

        view.ConsoleView.println(
                "Conducting workshop with "
                        + "training and instructional facilities."
        );
    }

    // PROTOTYPE: copy this event into a new, independent Workshop
    @Override
    public Event copyEventTemplate(String newEventId,
                                   String newEventName,
                                   String organizer,
                                   String newDate,
                                   String newVenue) {

        Workshop copy = new Workshop(
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