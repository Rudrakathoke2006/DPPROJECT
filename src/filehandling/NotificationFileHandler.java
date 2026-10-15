package filehandling;

import java.io.*;

public class NotificationFileHandler {

    private static final String FILE_PATH = "data/notifications.csv";
    private static final String HEADER = "eventId,eventName,recipient,channel,message";

    public void saveNotification(String eventId, String eventName, String recipient,
                                 String channel, String message) {
        ensureFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(clean(eventId) + "," + clean(eventName) + "," + clean(recipient) + ","
                    + clean(channel) + "," + clean(message));
        } catch (IOException e) {
            view.ConsoleView.println("Error saving notification: " + e.getMessage());
        }
    }

    public void displayNotificationsFor(String recipient) {
        ensureFile();
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();
            view.ConsoleView.println("\n======= NOTIFICATION HISTORY =======");
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 5 && data[2].equalsIgnoreCase(recipient)) {
                    found = true;
                    view.ConsoleView.println(data[0] + " | " + data[1] + " | " + data[3] + " | " + data[4]);
                }
            }
        } catch (IOException e) {
            view.ConsoleView.println("Error reading notifications: " + e.getMessage());
        }
        if (!found) view.ConsoleView.println("No notifications found.");
    }

    private void ensureFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) return;
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(HEADER);
        } catch (IOException e) {
            view.ConsoleView.println("Error creating notifications file: " + e.getMessage());
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }
}
