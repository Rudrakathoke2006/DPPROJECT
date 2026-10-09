package bridge;

public class InAppNotificationSender
        implements NotificationSender {

    @Override
    public void send(
            String recipient,
            String message) {

        System.out.println(
                "[IN-APP] User: "
                        + recipient
        );

        System.out.println(
                "Message: "
                        + message
        );
    }
}