package bridge;

public class SMSNotificationSender
        implements NotificationSender {

    @Override
    public void send(
            String recipient,
            String message) {

        System.out.println(
                "[SMS] To: "
                        + recipient
        );

        System.out.println(
                "Message: "
                        + message
        );
    }
}