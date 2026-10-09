package bridge;

import filehandling.NotificationFileHandler;

public class NotificationService {

    private final NotificationFileHandler fileHandler = new NotificationFileHandler();

    public void sendEventNotification(String eventId, String eventName, String recipient,
                                      String message, String channel) {
        NotificationSender sender = getSender(channel);
        if (sender == null) {
            System.out.println("Invalid notification channel.");
            return;
        }

        Notification notification = new EventNotification(sender);
        notification.sendNotification(recipient, message);
        fileHandler.saveNotification(eventId, eventName, recipient, channel.toUpperCase(), message);
    }

    // Backward-compatible overload for simple demonstrations.
    public void sendEventNotification(String recipient, String message, String channel) {
        sendEventNotification("N/A", "General Event Update", recipient, message, channel);
    }

    private NotificationSender getSender(String channel) {
        if (channel == null) return null;
        if (channel.equalsIgnoreCase("EMAIL")) return new EmailNotificationSender();
        if (channel.equalsIgnoreCase("SMS")) return new SMSNotificationSender();
        if (channel.equalsIgnoreCase("INAPP")) return new InAppNotificationSender();
        return null;
    }
}
