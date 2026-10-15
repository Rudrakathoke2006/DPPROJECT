package observer;

import filehandling.RegistrationFileHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EventNotificationManager {

    private final RegistrationFileHandler registrationFileHandler = new RegistrationFileHandler();

    public int notifyRegisteredStudents(String eventId, String eventName, String message) {
        EventUpdateSubject subject = new EventUpdateSubject();
        int count = 0;

        try (BufferedReader reader = new BufferedReader(
                new FileReader(registrationFileHandler.getFilePath()))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 5 && data[1].equalsIgnoreCase(eventId)) {
                    subject.registerObserver(new StudentObserver(data[3], data[4]));
                    count++;
                }
            }
        } catch (IOException e) {
            view.ConsoleView.println("Error loading event observers: " + e.getMessage());
            return 0;
        }

        if (count == 0) {
            view.ConsoleView.println("No registered students to notify.");
            return 0;
        }

        subject.notifyObservers(eventId, eventName, message);
        return count;
    }
}
