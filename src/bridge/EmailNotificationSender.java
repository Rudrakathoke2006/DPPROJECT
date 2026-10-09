package bridge;

public class EmailNotificationSender
        implements NotificationSender {

    @Override
    public void send(
            String recipient,
            String message) {

        System.out.println(
                "[EMAIL] To: "
                        + recipient
        );

        System.out.println(
                "Message: "
                        + message
        );
    }
}