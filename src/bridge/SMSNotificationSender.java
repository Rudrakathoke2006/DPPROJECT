package bridge;

public class SMSNotificationSender
        implements NotificationSender {

    @Override
    public void send(
            String recipient,
            String message) {

        view.ConsoleView.println(
                "[SMS] To: "
                        + recipient
        );

        view.ConsoleView.println(
                "Message: "
                        + message
        );
    }
}