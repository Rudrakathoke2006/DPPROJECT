package observer;

public interface EventObserver {

    void update(
            String eventId,
            String eventName,
            String message
    );
}