package filehandling;

import java.io.*;

public class RegistrationFileHandler {

    private static final String FILE_PATH = "data/registrations.csv";
    private static final String HEADER = "registrationId,eventId,eventName,studentUsername,notificationChannel";

    public boolean registerStudent(String eventId, String eventName, String studentUsername, String channel) {
        ensureFile();
        if (isRegistered(eventId, studentUsername)) {
            view.ConsoleView.println("Student is already registered for this event.");
            return false;
        }

        String registrationId = generateRegistrationId();
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(registrationId + "," + clean(eventId) + "," + clean(eventName) + ","
                    + clean(studentUsername) + "," + clean(channel.toUpperCase()));
            view.ConsoleView.println("Registration successful. Registration ID: " + registrationId);
            return true;
        } catch (IOException e) {
            view.ConsoleView.println("Error saving registration: " + e.getMessage());
            return false;
        }
    }

    public boolean isRegistered(String eventId, String studentUsername) {
        ensureFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 5 && data[1].equalsIgnoreCase(eventId)
                        && data[3].equalsIgnoreCase(studentUsername)) return true;
            }
        } catch (IOException e) {
            view.ConsoleView.println("Error reading registrations: " + e.getMessage());
        }
        return false;
    }

    public void displayStudentRegistrations(String username) {
        ensureFile();
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();
            view.ConsoleView.println("\n======= MY REGISTRATIONS =======");
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 5 && data[3].equalsIgnoreCase(username)) {
                    found = true;
                    view.ConsoleView.println(data[0] + " | " + data[1] + " | " + data[2] + " | " + data[4]);
                }
            }
        } catch (IOException e) {
            view.ConsoleView.println("Error reading registrations: " + e.getMessage());
        }
        if (!found) view.ConsoleView.println("No registrations found.");
    }

    public String getFilePath() {
        ensureFile();
        return FILE_PATH;
    }

    private String generateRegistrationId() {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            reader.readLine();
            while (reader.readLine() != null) count++;
        } catch (IOException e) {
            return "R001";
        }
        return String.format("R%03d", count + 1);
    }

    private void ensureFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) return;
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(HEADER);
        } catch (IOException e) {
            view.ConsoleView.println("Error creating registrations file: " + e.getMessage());
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }
}
