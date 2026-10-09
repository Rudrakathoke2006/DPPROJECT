package builder;

import factory.EventCreator;
import factory.EventCreatorProvider;
import filehandling.ApprovalFileHandler;
import filehandling.EventDetailsFileHandler;
import filehandling.EventFileHandler;
import filehandling.UserFileHandler;
import model.Event;
import model.User;

import java.util.Scanner;

/**
 * Console workflow for "Create Advanced Event" (acts as the Builder's director)
 * + the shared persistence step used by Builder and Prototype.
 */
public class AdvancedEventCreationService {

    private final EventFileHandler eventFileHandler = new EventFileHandler();
    private final ApprovalFileHandler approvalFileHandler = new ApprovalFileHandler();
    private final UserFileHandler userFileHandler = new UserFileHandler();
    private final EventDetailsFileHandler detailsFileHandler = new EventDetailsFileHandler();

    public void runConsoleFlow(User user, Scanner scanner) {
        System.out.println("\n===== CREATE ADVANCED EVENT (Builder) =====");
        System.out.println("1. Technical  2. Cultural  3. Sports  4. Workshop");
        System.out.print("Event type choice: ");
        EventCreator creator = EventCreatorProvider.forChoice(scanner.nextLine());
        if (creator == null) {
            System.out.println("Invalid event type.");
            return;
        }

        System.out.println("Event Level: 1. Department  2. Inter-Department  3. University");
        System.out.print("Event level choice: ");
        String level = levelFromChoice(scanner.nextLine().trim());
        if (level == null) {
            System.out.println("Invalid event level.");
            return;
        }

        System.out.print("Event ID: ");
        String id = scanner.nextLine().trim();
        if (eventFileHandler.eventExists(id)) {
            System.out.println("Event ID already exists.");
            return;
        }
        System.out.print("Event Name: ");
        String name = scanner.nextLine();
        System.out.print("Date (e.g. 15-03-2026): ");
        String date = scanner.nextLine();
        System.out.print("Proposed Venue: ");
        String venue = scanner.nextLine();

        System.out.println("\n--- Optional details (press Enter to skip) ---");
        System.out.print("Description: ");
        String description = scanner.nextLine();
        double budget = readNonNegativeDouble(scanner, "Budget: ");
        int capacity = readNonNegativeInt(scanner, "Expected attendees/capacity: ");
        int duration = readNonNegativeInt(scanner, "Duration (hours): ");
        System.out.print("Equipment needs (separate items with ';'): ");
        String equipment = scanner.nextLine();
        System.out.print("Special instructions: ");
        String instructions = scanner.nextLine();

        Event event;
        try {
            event = new EventBuilder(creator)
                    .withEventId(id)
                    .withName(name)
                    .withOrganizer(user.getName())
                    .withDate(date)
                    .withVenue(venue)
                    .withLevel(level)
                    .withDescription(description)
                    .withBudget(budget)
                    .withCapacity(capacity)
                    .withDuration(duration)
                    .withEquipment(equipment)
                    .withSpecialInstructions(instructions)
                    .build();
        } catch (IllegalArgumentException e) {
            System.out.println("Event not created: " + e.getMessage());
            return;
        }

        showSummary(event);
        System.out.print("\nSave this event? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Cancelled. Nothing was saved.");
            return;
        }

        if (submitNewEvent(event)) {
            System.out.println("Advanced event created with PENDING status and submitted for approval.");
        }
    }

    /**
     * Shared persistence step (used by Builder AND Prototype).
     * All checks happen BEFORE the first write, so a refused event leaves no partial data.
     * Always saves as PENDING and creates a fresh initial approval request.
     */
    public boolean submitNewEvent(Event event) {
        if (eventFileHandler.eventExists(event.getEventId())) {
            System.out.println("Event ID already exists. Event not saved.");
            return false;
        }
        if (approvalFileHandler.approvalExists(event.getEventId())) {
            System.out.println("An approval record already exists for this ID. Event not saved.");
            return false;
        }
        String firstApprover = userFileHandler.findUsernameByRole("FACULTY");
        if (firstApprover == null) {
            System.out.println("No Faculty approver is configured. Event not saved.");
            return false;
        }

        event.setStatus("PENDING");   // lifecycle always restarts at PENDING

        if (!eventFileHandler.saveEvent(event)) return false;
        detailsFileHandler.saveDetails(event.getEventId(), event.getSpecification());
        return approvalFileHandler.createInitialApproval(event, firstApprover);
    }

    public void showSummary(Event event) {
        System.out.println("\n========== EVENT SUMMARY ==========");
        event.displayDetails();
        event.getSpecification().display();
    }

    private String levelFromChoice(String choice) {
        if (choice.equals("1")) return "DEPARTMENT";
        if (choice.equals("2")) return "INTER_DEPARTMENT";
        if (choice.equals("3")) return "UNIVERSITY";
        return null;
    }

    private double readNonNegativeDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            if (text.isEmpty()) return 0;
            try {
                double value = Double.parseDouble(text);
                if (value >= 0 && !Double.isNaN(value) && !Double.isInfinite(value)) return value;
            } catch (NumberFormatException ignored) { }
            System.out.println("Enter a non-negative number, or press Enter to skip.");
        }
    }

    private int readNonNegativeInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            if (text.isEmpty()) return 0;
            try {
                int value = Integer.parseInt(text);
                if (value >= 0) return value;
            } catch (NumberFormatException ignored) { }
            System.out.println("Enter a non-negative whole number, or press Enter to skip.");
        }
    }
}