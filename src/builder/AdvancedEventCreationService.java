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
import filehandling.CsvTransaction;
import java.io.IOException;

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
        view.ConsoleView.println("\n===== CREATE ADVANCED EVENT (Builder) =====");
        view.ConsoleView.println("1. Technical  2. Cultural  3. Sports  4. Workshop");
        view.ConsoleView.print("Event type choice: ");
        EventCreator creator = EventCreatorProvider.forChoice(view.ConsoleView.readLine(scanner));
        if (creator == null) {
            view.ConsoleView.println("Invalid event type.");
            return;
        }

        view.ConsoleView.println("Event Level: 1. Department  2. Inter-Department  3. University");
        view.ConsoleView.print("Event level choice: ");
        String level = levelFromChoice(view.ConsoleView.readLine(scanner).trim());
        if (level == null) {
            view.ConsoleView.println("Invalid event level.");
            return;
        }

        view.ConsoleView.print("Event ID: ");
        String id = view.ConsoleView.readLine(scanner).trim();
        if (eventFileHandler.eventExists(id)) {
            view.ConsoleView.println("Event ID already exists.");
            return;
        }
        view.ConsoleView.print("Event Name: ");
        String name = view.ConsoleView.readLine(scanner);
        view.ConsoleView.print("Date (e.g. 15-03-2026): ");
        String date = view.ConsoleView.readLine(scanner);
        view.ConsoleView.print("Proposed Venue: ");
        String venue = view.ConsoleView.readLine(scanner);

        view.ConsoleView.println("\n--- Optional details (press Enter to skip) ---");
        view.ConsoleView.print("Description: ");
        String description = view.ConsoleView.readLine(scanner);
        double budget = readNonNegativeDouble(scanner, "Budget: ");
        int capacity = readNonNegativeInt(scanner, "Expected attendees/capacity: ");
        int duration = readNonNegativeInt(scanner, "Duration (hours): ");
        view.ConsoleView.print("Equipment needs (separate items with ';'): ");
        String equipment = view.ConsoleView.readLine(scanner);
        view.ConsoleView.print("Special instructions: ");
        String instructions = view.ConsoleView.readLine(scanner);

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
            view.ConsoleView.println("Event not created: " + e.getMessage());
            return;
        }

        showSummary(event);
        view.ConsoleView.print("\nSave this event? (yes/no): ");
        if (!view.ConsoleView.readLine(scanner).trim().equalsIgnoreCase("yes")) {
            view.ConsoleView.println("Cancelled. Nothing was saved.");
            return;
        }

        if (submitNewEvent(event)) {
            view.ConsoleView.println("Advanced event created with PENDING status and submitted for approval.");
        }
    }

    /**
     * Shared persistence step (used by Builder AND Prototype).
     * All checks happen BEFORE the first write, so a refused event leaves no partial data.
     * Always saves as PENDING and creates a fresh initial approval request.
     */
    public boolean submitNewEvent(Event event) {
        if (eventFileHandler.eventExists(event.getEventId())) {
            view.ConsoleView.println("Event ID already exists. Event not saved.");
            return false;
        }
        if (approvalFileHandler.approvalExists(event.getEventId())) {
            view.ConsoleView.println("An approval record already exists for this ID. Event not saved.");
            return false;
        }
        String firstApprover = userFileHandler.findUsernameByRole("FACULTY");
        if (firstApprover == null) {
            view.ConsoleView.println("No Faculty approver is configured. Event not saved.");
            return false;
        }

        event.setStatus("PENDING");   // lifecycle always restarts at PENDING

        try (CsvTransaction tx = new CsvTransaction("data/events.csv", "data/event_details.csv", "data/approvals.csv")) {
            if (!eventFileHandler.saveEvent(event)) return false;
            if (!detailsFileHandler.saveDetails(event.getEventId(), event.getSpecification())) return false;
            if (!approvalFileHandler.createInitialApproval(event, firstApprover)) return false;
            tx.commit();
            new mediator.UniversityEventCoordinator().eventCreated(event.getEventId());   // Mediator signal
            return true;
        } catch (IOException ex) {
            view.ConsoleView.println("Could not save all event records: " + ex.getMessage());
            return false;
        }
    }

    public void showSummary(Event event) {
        view.ConsoleView.println("\n========== EVENT SUMMARY ==========");
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
            view.ConsoleView.print(prompt);
            String text = view.ConsoleView.readLine(scanner).trim();
            if (text.isEmpty()) return 0;
            try {
                double value = Double.parseDouble(text);
                if (value >= 0 && !Double.isNaN(value) && !Double.isInfinite(value)) return value;
            } catch (NumberFormatException ignored) { }
            view.ConsoleView.println("Enter a non-negative number, or press Enter to skip.");
        }
    }

    private int readNonNegativeInt(Scanner scanner, String prompt) {
        while (true) {
            view.ConsoleView.print(prompt);
            String text = view.ConsoleView.readLine(scanner).trim();
            if (text.isEmpty()) return 0;
            try {
                int value = Integer.parseInt(text);
                if (value >= 0) return value;
            } catch (NumberFormatException ignored) { }
            view.ConsoleView.println("Enter a non-negative whole number, or press Enter to skip.");
        }
    }
}