package filehandling;

import model.Event;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class EventFileHandler {

    private static final String FILE_PATH = "data/events.csv";
    private static final String HEADER = "eventId,eventName,eventType,eventLevel,organizer,date,venue,status";

    public boolean saveEvent(Event event) {
        if (eventExists(event.getEventId())) {
            view.ConsoleView.println("Event ID already exists. Event not saved.");
            return false;
        }

        ensureFile();

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(
                    clean(event.getEventId()) + ","
                            + clean(event.getEventName()) + ","
                            + clean(event.getEventType()) + ","
                            + clean(event.getEventLevel()) + ","
                            + clean(event.getOrganizer()) + ","
                            + clean(event.getDate()) + ","
                            + clean(event.getVenue()) + ","
                            + clean(event.getStatus())
            );

            view.ConsoleView.println("Event saved to events.csv");
            return true;

        } catch (IOException e) {
            view.ConsoleView.println("Error saving event: " + e.getMessage());
            return false;
        }
    }

    public boolean eventExists(String eventId) {
        return findEventById(eventId) != null;
    }

    public String[] findEventById(String eventId) {
        ensureFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            reader.readLine(); // skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length >= 8
                        && data[0].equalsIgnoreCase(eventId)) {
                    return data;
                }
            }

        } catch (IOException e) {
            view.ConsoleView.println("Error reading events: " + e.getMessage());
        }

        return null;
    }

    public boolean isEventApproved(String eventId) {
        String[] data = findEventById(eventId);
        return data != null && data[7].equalsIgnoreCase("APPROVED");
    }

    public boolean updateEventStatus(String eventId, String newStatus) {
        ensureFile();

        File original = new File(FILE_PATH);
        File temp = new File("data/events_temp.csv");
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(original));
             PrintWriter writer = new PrintWriter(new FileWriter(temp))) {

            writer.println(HEADER);
            reader.readLine(); // skip original header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length >= 8
                        && data[0].equalsIgnoreCase(eventId)) {
                    data[7] = newStatus;
                    writer.println(joinEvent(data));
                    found = true;
                } else if (data.length >= 8) {
                    writer.println(line);
                }
            }

        } catch (IOException e) {
            view.ConsoleView.println("Error updating event status: " + e.getMessage());
            deleteTempFile(temp);
            return false;
        }

        if (!found) {
            view.ConsoleView.println("Event not found while updating status: " + eventId);
            deleteTempFile(temp);
            return false;
        }

        return replaceFile(original, temp);
    }

    public void displayAllEvents() {
        displayEvents(false);
    }

    public void displayApprovedEvents() {
        displayEvents(true);
    }

    private void displayEvents(boolean approvedOnly) {
        ensureFile();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            reader.readLine(); // skip header

            view.ConsoleView.println(
                    approvedOnly
                            ? "\n========= APPROVED EVENTS ========="
                            : "\n========= ALL EVENTS ========="
            );

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                if (data.length < 8) {
                    continue;
                }

                if (approvedOnly
                        && !data[7].equalsIgnoreCase("APPROVED")) {
                    continue;
                }

                found = true;

                view.ConsoleView.println("\nEvent ID   : " + data[0]);
                view.ConsoleView.println("Event Name : " + data[1]);
                view.ConsoleView.println("Type       : " + data[2]);
                view.ConsoleView.println("Level      : " + data[3]);
                view.ConsoleView.println("Organizer  : " + data[4]);
                view.ConsoleView.println("Date       : " + data[5]);
                view.ConsoleView.println("Venue      : " + data[6]);
                view.ConsoleView.println("Status     : " + data[7]);
            }

        } catch (IOException e) {
            view.ConsoleView.println("Error reading events: " + e.getMessage());
        }

        if (!found) {
            view.ConsoleView.println("No matching events found.");
        }
    }

    private void ensureFile() {
        File file = new File(FILE_PATH);

        if (file.exists()) {
            return;
        }

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(HEADER);
        } catch (IOException e) {
            view.ConsoleView.println("Error creating events file: " + e.getMessage());
        }
    }

    private String joinEvent(String[] d) {
        return clean(d[0]) + ","
                + clean(d[1]) + ","
                + clean(d[2]) + ","
                + clean(d[3]) + ","
                + clean(d[4]) + ","
                + clean(d[5]) + ","
                + clean(d[6]) + ","
                + clean(d[7]);
    }

    private String clean(String value) {
        return value == null
                ? ""
                : value.replace(",", " ")
                       .replace("\n", " ")
                       .replace("\r", " ");
    }

    private boolean replaceFile(File original, File temp) {
        try {
            Files.move(
                    temp.toPath(),
                    original.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
            return true;

        } catch (IOException e) {
            view.ConsoleView.println("Could not replace events file: " + e.getMessage());
            deleteTempFile(temp);
            return false;
        }
    }

    private void deleteTempFile(File temp) {
        if (temp.exists() && !temp.delete()) {
            view.ConsoleView.println("Warning: temporary events file could not be deleted.");
        }
    }
}
