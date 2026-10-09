package bridge;

public interface NotificationSender {

    void send(
            String recipient,
            String message
    );
}