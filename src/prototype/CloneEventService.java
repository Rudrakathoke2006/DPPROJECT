package prototype;

import builder.AdvancedEventCreationService;
import builder.EventValidator;
import filehandling.EventFileHandler;
import model.Event;
import model.User;

import java.util.Scanner;

/**
 * "Duplicate Existing Event": applies overrides, resets lifecycle, persists the clone.
 */
public class CloneEventService {

    private final EventFileHandler eventFileHandler = new EventFileHandler();
    private final EventCloner cloner = new EventCloner();
    private final AdvancedEventCreationService submission = new AdvancedEventCreationService();

    public void runConsoleFlow(User user, Scanner scanner) {
        System.out.println("\n===== DUPLICATE EXISTING EVENT (Prototype) =====");
        eventFileHandler.displayAllEvents();

        System.out.print("\nSource Event ID: ");
        String sourceId = scanner.nextLine().trim();
        Event source = cloner.loadEvent(sourceId);
        if (source == null) {
            System.out.println("Source event not found.");
            return;
        }

        System.out.println("\n--- Reusable fields from source " + source.getEventId() + " ---");
        System.out.println("Type                : " + source.getEventType());
        System.out.println("Level               : " + source.getEventLevel());
        System.out.println("Source venue        : " + source.getVenue());
        source.getSpecification().copyAsTemplate().display();

        System.out.print("\nNEW Event ID: ");
        String newId = scanner.nextLine().trim();
        System.out.print("New Event Name (Enter = \"" + source.getEventName() + " (Copy)\"): ");
        String newName = scanner.nextLine();
        System.out.print("New Date (e.g. 15-03-2026): ");
        String newDate = scanner.nextLine();
        System.out.print("New Venue (Enter = keep \"" + source.getVenue() + "\"): ");
        String newVenue = scanner.nextLine();

        Event clone;
        try {
            clone = prepareClone(sourceId, newId, newName, user.getName(), newDate, newVenue);
        } catch (IllegalArgumentException e) {
            System.out.println("Duplicate failed: " + e.getMessage());
            return;
        }

        submission.showSummary(clone);
        System.out.print("\nSave this duplicate? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Cancelled. Nothing was saved.");
            return;
        }

        if (submission.submitNewEvent(clone)) {
            System.out.println("Duplicate created with PENDING status and a fresh approval request.");
        }
    }

    /**
     * Builds the clone in memory (NO file writes).
     * @throws IllegalArgumentException if source is missing, new ID exists, or a field is invalid
     */
    public Event prepareClone(String sourceId, String newId, String newName,
                              String organizer, String newDate, String newVenue) {

        Event source = cloner.loadEvent(sourceId);
        if (source == null) {
            throw new IllegalArgumentException("Source event not found: " + sourceId);
        }
        if (newId != null && eventFileHandler.eventExists(newId.trim())) {
            throw new IllegalArgumentException("Event ID already exists: " + newId.trim());
        }

        String name = isBlank(newName) ? source.getEventName() + " (Copy)" : newName;
        String venue = isBlank(newVenue) ? source.getVenue() : newVenue;

        EventValidator.validateCore(newId, name, organizer, newDate, venue, source.getEventLevel());

        return cloner.cloneEvent(source, newId.trim(), name.trim(), organizer.trim(),
                newDate.trim(), venue.trim());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}