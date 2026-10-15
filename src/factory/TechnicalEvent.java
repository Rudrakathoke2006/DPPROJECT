package factory;

import model.Event;

public class TechnicalEvent extends Event {

    public TechnicalEvent(String eventId,
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
                "TECHNICAL"
        );
    }

    @Override
    public void conductEvent() {

        view.ConsoleView.println(
                "Conducting technical event with "
                        + "technical equipment and facilities."
        );
    }

    // PROTOTYPE: copy this event into a new, independent TechnicalEvent
    @Override
    public Event copyEventTemplate(String newEventId,
                                   String newEventName,
                                   String organizer,
                                   String newDate,
                                   String newVenue) {

        TechnicalEvent copy = new TechnicalEvent(
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