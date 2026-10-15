package controller;

import abstractfactory.*;
import builder.AdvancedEventCreationService;
import chain.ApprovalManager;
import factory.*;
import filehandling.*;
import model.Event;
import model.User;
import observer.EventNotificationManager;
import prototype.CloneEventService;
import proxy.EventService;
import resources.Equipment;
import resources.SupportStaff;
import resources.Venue;
import singleton.FestManagementSystem;

import java.util.Scanner;

public final class EventWorkflowController {
    private EventWorkflowController() {}
    public static void createEvent(FestManagementSystem system, User user, Scanner scanner) {
        view.ConsoleView.println("\n1. Technical  2. Cultural  3. Sports  4. Workshop");
        view.ConsoleView.print("Event type choice: ");
        String typeChoice = view.ConsoleView.readLine(scanner);
        EventCreator creator = getCreator(typeChoice);
        if (creator == null) {
            view.ConsoleView.println("Invalid event type.");
            return;
        }

        view.ConsoleView.print("Event ID: ");
        String id = view.ConsoleView.readLine(scanner).trim();
        if (system.getEventFileHandler().eventExists(id)) {
            view.ConsoleView.println("Event ID already exists.");
            return;
        }
        view.ConsoleView.print("Event Name: ");
        String name = view.ConsoleView.readLine(scanner);
        view.ConsoleView.print("Date: ");
        String date = view.ConsoleView.readLine(scanner);
        view.ConsoleView.print("Proposed Venue: ");
        String venue = view.ConsoleView.readLine(scanner);

        view.ConsoleView.println("Event Level: 1. Department  2. Inter-Department  3. University");
        view.ConsoleView.print("Event level choice: ");
        String level = getEventLevel(view.ConsoleView.readLine(scanner));
        if (level == null) {
            view.ConsoleView.println("Invalid event level.");
            return;
        }

        Event event = creator.prepareEvent(id, name, user.getName(), date, venue);
        event.setEventLevel(level);
        if (system.getEventFileHandler().saveEvent(event)) {
            // Mediator sets up approval routing (exactly one request) through the existing approval storage.
            if (system.getEventCoordinator().eventCreated(id)) {
                view.ConsoleView.println("Event created with PENDING status and submitted for approval.");
            } else {
                view.ConsoleView.println("Event saved, but the approval request could not be created.");
            }
        }
    }

    public static void registerForEvent(FestManagementSystem system, User user, Scanner scanner) {
        system.getEventFileHandler().displayApprovedEvents();
        view.ConsoleView.print("Enter Event ID: ");
        String id = view.ConsoleView.readLine(scanner);
        String[] event = system.getEventFileHandler().findEventById(id);
        if (event == null || !event[7].equalsIgnoreCase("APPROVED")) {
            view.ConsoleView.println("Only approved events are open for registration.");
            return;
        }
        view.ConsoleView.print("Notification channel (EMAIL/SMS/INAPP): ");
        String channel = view.ConsoleView.readLine(scanner).toUpperCase();
        if (!validChannel(channel)) {
            view.ConsoleView.println("Invalid channel.");
            return;
        }
        if (system.getRegistrationFileHandler().registerStudent(event[0], event[1], user.getUsername(), channel)) {
            system.getEventCoordinator().registrationCompleted(event[0], user.getUsername());
        }
    }

    public static void allocateResources(FestManagementSystem system, Scanner scanner) {
        system.getEventFileHandler().displayApprovedEvents();
        view.ConsoleView.print("Enter approved Event ID: ");
        String id = view.ConsoleView.readLine(scanner);
        String[] event = system.getEventFileHandler().findEventById(id);
        if (event == null || !event[7].equalsIgnoreCase("APPROVED")) {
            view.ConsoleView.println("Resources can be allocated only to approved events.");
            return;
        }
        if (system.getResourceFileHandler().resourceExists(id)) {
            view.ConsoleView.println("Resources already allocated.");
            return;
        }
        ResourceFactory factory = getResourceFactory(event[2]);
        if (factory == null) {
            view.ConsoleView.println("Unsupported event type.");
            return;
        }
        Venue venue = factory.createVenue();
        Equipment equipment = factory.createEquipment();
        SupportStaff staff = factory.createSupportStaff();
        venue.displayDetails();
        equipment.displayDetails();
        staff.displayDetails();
        if (system.getResourceFileHandler().saveResources(event[0], event[1], event[2], venue, equipment, staff)) {
            system.getEventCoordinator().resourcesAllocated(event[0], venue.getVenueName());
        }
    }

    public static void smartVenueSelection(FestManagementSystem system, User user, Scanner scanner) {
        view.ConsoleView.println("Strategy chooses the physical venue; Abstract Factory supplies category resources.");
        view.ConsoleView.print("Event ID for resource factory lookup: ");
        String id = view.ConsoleView.readLine(scanner).trim();
        String[] event = system.getEventFileHandler().findEventById(id);
        if (event == null || !"APPROVED".equalsIgnoreCase(event[7])) {
            view.ConsoleView.println("Only approved events are eligible."); return;
        }
        // Keep the selected event ID for the Strategy flow.
        // A dedicated overload accepts the prevalidated ID and original scanner.
        new strategy.SmartVenueSelectionService().runConsoleFlow(user, scanner, getResourceFactory(event[2]), id);
    }

    public static void publishUpdate(FestManagementSystem system, Scanner scanner) {
        system.getEventFileHandler().displayApprovedEvents();
        view.ConsoleView.print("Enter approved Event ID: ");
        String id = view.ConsoleView.readLine(scanner);
        String[] event = system.getEventFileHandler().findEventById(id);
        if (event == null || !event[7].equalsIgnoreCase("APPROVED")) {
            view.ConsoleView.println("Updates can be published only for approved events.");
            return;
        }
        view.ConsoleView.print("Update message: ");
        String message = view.ConsoleView.readLine(scanner);
        int count = system.getEventCoordinator().eventUpdated(event[0], message);   // Mediator -> Observer + Bridge
        view.ConsoleView.println("Observers notified: " + count);
    }

    public static void showCoordinationStatus(FestManagementSystem system, User user, Scanner scanner) {
        if (!system.getEventService().viewCoordinationStatus(user)) return;   // Proxy access check
        boolean studentView = "STUDENT".equalsIgnoreCase(user.getRole());
        view.ConsoleView.print("Event ID (leave blank for all" + (studentView ? " approved" : "") + " events): ");
        String id = view.ConsoleView.readLine(scanner).trim();
        view.ConsoleView.println("\n========= EVENT COORDINATION STATUS (Mediator) =========");
        if (id.isEmpty()) {
            java.util.List<mediator.EventCoordinationStatus> all = system.getEventCoordinator().allStatuses(studentView);
            if (all.isEmpty()) view.ConsoleView.println("No matching events found.");
            for (mediator.EventCoordinationStatus s : all) s.display();
            return;
        }
        mediator.EventCoordinationStatus s = system.getEventCoordinator().statusOf(id);
        if (s == null || (studentView && !"APPROVED".equalsIgnoreCase(s.eventStatus))) {
            view.ConsoleView.println(studentView ? "No approved event found with that ID." : "Event not found.");
            return;
        }
        s.display();
    }

    private static EventCreator getCreator(String choice) {
        if (choice.equals("1")) return new TechnicalEventCreator();
        if (choice.equals("2")) return new CulturalEventCreator();
        if (choice.equals("3")) return new SportsEventCreator();
        if (choice.equals("4")) return new WorkshopCreator();
        return null;
    }

    private static ResourceFactory getResourceFactory(String type) {
        if (type.equalsIgnoreCase("TECHNICAL")) return new TechnicalResourceFactory();
        if (type.equalsIgnoreCase("CULTURAL")) return new CulturalResourceFactory();
        if (type.equalsIgnoreCase("SPORTS")) return new SportsResourceFactory();
        if (type.equalsIgnoreCase("WORKSHOP")) return new WorkshopResourceFactory();
        return null;
    }

    private static String getEventLevel(String choice) {
        if (choice.equals("1")) return "DEPARTMENT";
        if (choice.equals("2")) return "INTER_DEPARTMENT";
        if (choice.equals("3")) return "UNIVERSITY";
        return null;
    }

    private static boolean validChannel(String channel) {
        return channel.equals("EMAIL") || channel.equals("SMS") || channel.equals("INAPP");
    }
}
