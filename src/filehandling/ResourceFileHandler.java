package filehandling;

import resources.Equipment;
import resources.SupportStaff;
import resources.Venue;

import java.io.*;

public class ResourceFileHandler {

    private static final String FILE_PATH = "data/resources.csv";
    private static final String HEADER = "eventId,eventName,eventType,venue,equipment,supportStaff";

    public boolean resourceExists(String eventId) {
        ensureFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 6 && data[0].equalsIgnoreCase(eventId)) return true;
            }
        } catch (IOException e) {
            view.ConsoleView.println("Error reading resources: " + e.getMessage());
        }
        return false;
    }

    public boolean saveResources(String eventId, String eventName, String eventType,
                                 Venue venue, Equipment equipment, SupportStaff supportStaff) {
        ensureFile();
        if (resourceExists(eventId)) {
            view.ConsoleView.println("Resources are already allocated to this event.");
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            writer.println(clean(eventId) + "," + clean(eventName) + "," + clean(eventType) + ","
                    + clean(venue.getVenueName()) + "," + clean(equipment.getEquipmentName()) + ","
                    + clean(supportStaff.getStaffType()));
            view.ConsoleView.println("\nResources saved successfully.");
            return true;
        } catch (IOException e) {
            view.ConsoleView.println("Error saving resources: " + e.getMessage());
            return false;
        }
    }

    private void ensureFile() {
        File file = new File(FILE_PATH);
        if (file.exists()) return;
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(HEADER);
        } catch (IOException e) {
            view.ConsoleView.println("Error creating resources file: " + e.getMessage());
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }
}
