package filehandling;

import model.EventSpecification;

import java.io.*;

/**
 * Stores the optional event details in data/event_details.csv (keyed by eventId).
 * events.csv is NOT touched, so existing readers/writers keep working.
 */
public class EventDetailsFileHandler {

    private static final String FILE_PATH = "data/event_details.csv";
    private static final String HEADER =
            "eventId,description,budget,capacity,durationHours,equipment,specialInstructions";

    public boolean saveDetails(String eventId, EventSpecification spec) {
        ensureFile();
        if (detailsExist(eventId)) {
            System.out.println("Event details already exist for this event.");
            return false;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(EventSpecification.sanitize(eventId) + ","
                    + spec.getDescription() + ","
                    + spec.getBudget() + ","
                    + spec.getCapacity() + ","
                    + spec.getDurationHours() + ","
                    + spec.getEquipment() + ","
                    + spec.getSpecialInstructions());
            return true;
        } catch (IOException e) {
            System.out.println("Error saving event details: " + e.getMessage());
            return false;
        }
    }

    public boolean detailsExist(String eventId) {
        return readRow(eventId) != null;
    }

    /** Returns the saved details, or default details if the event has none (e.g. basic create). */
    public EventSpecification findDetails(String eventId) {
        String[] d = readRow(eventId);
        if (d == null) return EventSpecification.defaults();
        try {
            return new EventSpecification(d[1], Double.parseDouble(d[2]),
                    Integer.parseInt(d[3]), Integer.parseInt(d[4]), d[5], d[6]);
        } catch (IllegalArgumentException e) {   // includes NumberFormatException
            return EventSpecification.defaults();
        }
    }

    private String[] readRow(String eventId) {
        ensureFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = line.split(",", -1);
                if (d.length >= 7 && d[0].equalsIgnoreCase(eventId)) return d;
            }
        } catch (IOException e) {
            System.out.println("Error reading event details: " + e.getMessage());
        }
        return null;
    }

    private void ensureFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) return;
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(HEADER);
        } catch (IOException e) {
            System.out.println("Error creating event details file: " + e.getMessage());
        }
    }
}
