package observer;

import bridge.NotificationService;

public class HODObserver
        implements EventObserver {

    private String username;
    private String notificationChannel;

    private NotificationService
            notificationService;

    public HODObserver(
            String username,
            String notificationChannel) {

        this.username = username;
        this.notificationChannel =
                notificationChannel;

        notificationService =
                new NotificationService();
    }

    @Override
    public void update(
            String eventId,
            String eventName,
            String message) {

        String fullMessage =
                "Event: "
                        + eventName
                        + " ("
                        + eventId
                        + ")\n"
                        + message;

        notificationService
                .sendEventNotification(
                        eventId,
                        eventName,
                        username,
                        fullMessage,
                        notificationChannel
                );
    }
}