package bridge;

public class EventNotification
        extends Notification {

    public EventNotification(
            NotificationSender sender) {

        super(sender);
    }

    @Override
    public void sendNotification(
            String recipient,
            String message) {

        String formattedMessage =
                "UNIVERSITY EVENT UPDATE\n"
                        + "----------------------\n"
                        + message;

        sender.send(
                recipient,
                formattedMessage
        );
    }
}