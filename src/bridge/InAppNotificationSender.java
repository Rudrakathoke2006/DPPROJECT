package bridge;

public class InAppNotificationSender
        implements NotificationSender {

    @Override
    public void send(
            String recipient,
            String message) {

        view.ConsoleView.println(
                "[IN-APP] User: "
                        + recipient
        );

        view.ConsoleView.println(
                "Message: "
                        + message
        );
    }
}