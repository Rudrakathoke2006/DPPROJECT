package observer;

public class EventUpdateSubject {

    private static final int MAX_OBSERVERS = 50;

    private EventObserver[] observers;
    private int observerCount;

    public EventUpdateSubject() {

        observers =
                new EventObserver[MAX_OBSERVERS];

        observerCount = 0;
    }

    public void registerObserver(
            EventObserver observer) {

        if (observer == null) {
            return;
        }

        if (observerCount >= MAX_OBSERVERS) {

            view.ConsoleView.println(
                    "Maximum observers reached."
            );

            return;
        }

        observers[observerCount] = observer;
        observerCount++;

        view.ConsoleView.println(
                "Observer registered successfully."
        );
    }

    public void removeObserver(
            EventObserver observer) {

        for (int i = 0;
             i < observerCount;
             i++) {

            if (observers[i] == observer) {

                for (int j = i;
                     j < observerCount - 1;
                     j++) {

                    observers[j] = observers[j + 1];
                }

                observers[observerCount - 1] = null;

                observerCount--;

                return;
            }
        }
    }

    public void notifyObservers(
            String eventId,
            String eventName,
            String message) {

        for (int i = 0;
             i < observerCount;
             i++) {

            observers[i].update(
                    eventId,
                    eventName,
                    message
            );
        }
    }
}