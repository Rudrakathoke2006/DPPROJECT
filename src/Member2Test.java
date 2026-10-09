import factory.CulturalEventCreator;
import factory.EventCreator;
import factory.SportsEventCreator;
import factory.TechnicalEventCreator;
import factory.WorkshopCreator;
import filehandling.ApprovalFileHandler;
import filehandling.EventFileHandler;
import filehandling.UserFileHandler;
import model.Event;

import java.util.Scanner;

public class Member2Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EventFileHandler eventFileHandler = new EventFileHandler();

        System.out.println("========================================");
        System.out.println("   UNIVERSITY EVENT MANAGEMENT SYSTEM");
        System.out.println("========================================");
        System.out.println("\nSelect Event Type:");
        System.out.println("1. Technical Event");
        System.out.println("2. Cultural Event");
        System.out.println("3. Sports Event");
        System.out.println("4. Workshop");
        System.out.print("\nEnter choice: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            scanner.close();
            return;
        }

        System.out.print("Enter Event ID: ");
        String eventId = scanner.nextLine().trim();
        if (eventFileHandler.eventExists(eventId)) {
            System.out.println("Event ID already exists. Please use a unique Event ID.");
            scanner.close();
            return;
        }

        System.out.print("Enter Event Name: ");
        String eventName = scanner.nextLine();
        System.out.print("Enter Organizer: ");
        String organizer = scanner.nextLine();
        System.out.print("Enter Date: ");
        String date = scanner.nextLine();
        System.out.print("Enter Venue: ");
        String venue = scanner.nextLine();
        System.out.println("Event Level: 1. Department  2. Inter-Department  3. University");
        System.out.print("Enter level choice: ");
        String levelChoice = scanner.nextLine();
        String eventLevel = levelChoice.equals("1") ? "DEPARTMENT" : levelChoice.equals("2") ? "INTER_DEPARTMENT" : levelChoice.equals("3") ? "UNIVERSITY" : null;
        if (eventLevel == null) { System.out.println("Invalid event level."); scanner.close(); return; }

        EventCreator creator = getCreator(choice);
        if (creator == null) {
            System.out.println("Invalid choice.");
            scanner.close();
            return;
        }

        Event event = creator.prepareEvent(eventId, eventName, organizer, date, venue);
        event.setEventLevel(eventLevel);
        event.displayDetails();
        event.conductEvent();

        if (eventFileHandler.saveEvent(event)) {
            UserFileHandler userFileHandler = new UserFileHandler();
            String facultyApprover = userFileHandler.findUsernameByRole("FACULTY");
            ApprovalFileHandler approvalFileHandler = new ApprovalFileHandler();
            approvalFileHandler.createInitialApproval(event, facultyApprover);
            System.out.println("Event submitted to the approval workflow.");
        }

        System.out.print("\nDo you want to view all events? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) eventFileHandler.displayAllEvents();
        scanner.close();
    }

    private static EventCreator getCreator(int choice) {
        switch (choice) {
            case 1: return new TechnicalEventCreator();
            case 2: return new CulturalEventCreator();
            case 3: return new SportsEventCreator();
            case 4: return new WorkshopCreator();
            default: return null;
        }
    }
}
