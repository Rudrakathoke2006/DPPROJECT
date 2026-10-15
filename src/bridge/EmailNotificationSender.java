package bridge;

public class EmailNotificationSender
        implements NotificationSender {

    @Override
    public void send(
            String recipient,
            String message) {

        view.ConsoleView.println(
                "[EMAIL] To: "
                        + recipient
        );

        view.ConsoleView.println(
                "Message: "
                        + message
        );
    }
}