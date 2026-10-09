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

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FestManagementSystem system = FestManagementSystem.getInstance();

        boolean applicationRunning = true;
        while (applicationRunning) {
            System.out.println("\n================================================");
            System.out.println(" SMART UNIVERSITY EVENT & FEST MANAGEMENT SYSTEM");
            System.out.println("================================================");
            System.out.println("1. Login");
            System.out.println("0. Exit Application");
            System.out.print("Enter choice: ");
            String mainChoice = scanner.nextLine().trim();

            if (mainChoice.equals("0")) {
                applicationRunning = false;
                continue;
            }
            if (!mainChoice.equals("1")) {
                System.out.println("Invalid choice.");
                continue;
            }

            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = system.getAuthenticationManager().login(username, password);
            if (user == null) {
                System.out.println("Returning to main menu...");
                continue;
            }

            boolean loggedIn = true;
            while (loggedIn) {
                showMenu(user.getRole());
                System.out.print("Enter choice: ");
                String choice = scanner.nextLine();

                if (user.getRole().equals("STUDENT")) {
                    loggedIn = studentAction(system, user, choice, scanner);
                } else if (user.getRole().equals("FACULTY")) {
                    loggedIn = facultyAction(system, user, choice, scanner);
                } else if (user.getRole().equals("HOD") || user.getRole().equals("DEAN")) {
                    loggedIn = authorityAction(system, user, choice, scanner);
                } else if (user.getRole().equals("ADMIN")) {
                    loggedIn = adminAction(system, user, choice, scanner);
                } else {
                    System.out.println("Unsupported role.");
                    loggedIn = false;
                }
            }

            System.out.println("Logged out successfully. Returning to main menu...");
        }

        System.out.println("Application closed. Thank you.");
        scanner.close();
    }

    private static void showMenu(String role) {
        System.out.println("\n---------------- " + role + " MENU ----------------");
        if (role.equals("STUDENT")) {
            System.out.println("1. View approved events");
            System.out.println("2. Register for event");
            System.out.println("3. View my registrations");
            System.out.println("4. View notification history");
            System.out.println("0. Logout");
        } else if (role.equals("FACULTY")) {
            System.out.println("1. View all events");
            System.out.println("2. Create event");
            System.out.println("3. Process pending approvals");
            System.out.println("4. Allocate resources to approved event");
            System.out.println("5. Publish event update");
            System.out.println("6. Create advanced event (Builder)");
            System.out.println("7. Duplicate existing event (Prototype)");
            System.out.println("8. Edit event (Memento)");
            System.out.println("9. Undo last event edit (Memento)");
            System.out.println("10. View event edit history");
            System.out.println("0. Logout");
        } else if (role.equals("HOD") || role.equals("DEAN")) {
            System.out.println("1. View all events");
            System.out.println("2. Process pending approvals");
            System.out.println("3. Publish event update");
            System.out.println("0. Logout");
        } else if (role.equals("ADMIN")) {
            System.out.println("1. View all events");
            System.out.println("2. Create event");
            System.out.println("3. Allocate resources to approved event");
            System.out.println("4. Publish event update");
            System.out.println("0. Logout");
        }
    }

    private static boolean studentAction(FestManagementSystem system, User user,
                                         String choice, Scanner scanner) {
        if (choice.equals("0")) return false;
        if (choice.equals("1")) {
            system.getEventService().viewEvents(user);
        } else if (choice.equals("2")) {
            system.getEventService().registerForEvent(user); // Proxy access check
            registerForEvent(system, user, scanner);
        } else if (choice.equals("3")) {
            system.getRegistrationFileHandler().displayStudentRegistrations(user.getUsername());
        } else if (choice.equals("4")) {
            system.getNotificationFileHandler().displayNotificationsFor(user.getUsername());
        } else {
            System.out.println("Invalid choice.");
        }
        return true;
    }

    private static boolean facultyAction(FestManagementSystem system, User user,
                                         String choice, Scanner scanner) {
        if (choice.equals("0")) return false;
        if (choice.equals("1")) {
            system.getEventService().viewEvents(user);
        } else if (choice.equals("2")) {
            system.getEventService().createEvent(user); // Proxy access check
            createEvent(system, user, scanner);
        } else if (choice.equals("3")) {
            system.getApprovalManager().showPendingApprovals(user.getUsername(), user.getRole(), scanner);
        } else if (choice.equals("4")) {
            allocateResources(system, scanner);
        } else if (choice.equals("5")) {
            publishUpdate(system, scanner);
        } else if (choice.equals("6")) {
            if (system.getEventService().createAdvancedEvent(user)) {   // Proxy access check
                new AdvancedEventCreationService().runConsoleFlow(user, scanner);
            }
        } else if (choice.equals("7")) {
            if (system.getEventService().duplicateEvent(user)) {        // Proxy access check
                new CloneEventService().runConsoleFlow(user, scanner);
            }
        } else if (choice.equals("8")) {
            new memento.EventEditService().edit(user, scanner);
        } else if (choice.equals("9")) {
            new memento.EventEditService().undoLast(user, scanner);
        } else if (choice.equals("10")) {
            new memento.EventEditService().showHistory(user, scanner);
        } else {
            System.out.println("Invalid choice.");
        }
        return true;
    }

    private static boolean authorityAction(FestManagementSystem system, User user,
                                           String choice, Scanner scanner) {
        if (choice.equals("0")) return false;
        if (choice.equals("1")) {
            system.getEventService().viewEvents(user);
        } else if (choice.equals("2")) {
            system.getApprovalManager().showPendingApprovals(user.getUsername(), user.getRole(), scanner);
        } else if (choice.equals("3")) {
            publishUpdate(system, scanner);
        } else {
            System.out.println("Invalid choice.");
        }
        return true;
    }

    private static boolean adminAction(FestManagementSystem system, User user,
                                       String choice, Scanner scanner) {
        if (choice.equals("0")) return false;
        if (choice.equals("1")) {
            system.getEventService().viewEvents(user);
        } else if (choice.equals("2")) {
            system.getEventService().createEvent(user);
            createEvent(system, user, scanner);
        } else if (choice.equals("3")) {
            allocateResources(system, scanner);
        } else if (choice.equals("4")) {
            publishUpdate(system, scanner);
        } else {
            System.out.println("Invalid choice.");
        }
        return true;
    }

    private static void createEvent(FestManagementSystem system, User user, Scanner scanner) {
        System.out.println("\n1. Technical  2. Cultural  3. Sports  4. Workshop");
        System.out.print("Event type choice: ");
        String typeChoice = scanner.nextLine();
        EventCreator creator = getCreator(typeChoice);
        if (creator == null) {
            System.out.println("Invalid event type.");
            return;
        }

        System.out.print("Event ID: ");
        String id = scanner.nextLine().trim();
        if (system.getEventFileHandler().eventExists(id)) {
            System.out.println("Event ID already exists.");
            return;
        }
        System.out.print("Event Name: ");
        String name = scanner.nextLine();
        System.out.print("Date: ");
        String date = scanner.nextLine();
        System.out.print("Proposed Venue: ");
        String venue = scanner.nextLine();

        System.out.println("Event Level: 1. Department  2. Inter-Department  3. University");
        System.out.print("Event level choice: ");
        String level = getEventLevel(scanner.nextLine());
        if (level == null) {
            System.out.println("Invalid event level.");
            return;
        }

        Event event = creator.prepareEvent(id, name, user.getName(), date, venue);
        event.setEventLevel(level);
        if (system.getEventFileHandler().saveEvent(event)) {
            String firstApprover = new UserFileHandler().findUsernameByRole("FACULTY");
            new ApprovalFileHandler().createInitialApproval(event, firstApprover);
            System.out.println("Event created with PENDING status and submitted for approval.");
        }
    }

    private static void registerForEvent(FestManagementSystem system, User user, Scanner scanner) {
        system.getEventFileHandler().displayApprovedEvents();
        System.out.print("Enter Event ID: ");
        String id = scanner.nextLine();
        String[] event = system.getEventFileHandler().findEventById(id);
        if (event == null || !event[7].equalsIgnoreCase("APPROVED")) {
            System.out.println("Only approved events are open for registration.");
            return;
        }
        System.out.print("Notification channel (EMAIL/SMS/INAPP): ");
        String channel = scanner.nextLine().toUpperCase();
        if (!validChannel(channel)) {
            System.out.println("Invalid channel.");
            return;
        }
        system.getRegistrationFileHandler().registerStudent(event[0], event[1], user.getUsername(), channel);
    }

    private static void allocateResources(FestManagementSystem system, Scanner scanner) {
        system.getEventFileHandler().displayApprovedEvents();
        System.out.print("Enter approved Event ID: ");
        String id = scanner.nextLine();
        String[] event = system.getEventFileHandler().findEventById(id);
        if (event == null || !event[7].equalsIgnoreCase("APPROVED")) {
            System.out.println("Resources can be allocated only to approved events.");
            return;
        }
        if (system.getResourceFileHandler().resourceExists(id)) {
            System.out.println("Resources already allocated.");
            return;
        }
        ResourceFactory factory = getResourceFactory(event[2]);
        if (factory == null) {
            System.out.println("Unsupported event type.");
            return;
        }
        Venue venue = factory.createVenue();
        Equipment equipment = factory.createEquipment();
        SupportStaff staff = factory.createSupportStaff();
        venue.displayDetails();
        equipment.displayDetails();
        staff.displayDetails();
        system.getResourceFileHandler().saveResources(event[0], event[1], event[2], venue, equipment, staff);
    }

    private static void publishUpdate(FestManagementSystem system, Scanner scanner) {
        system.getEventFileHandler().displayApprovedEvents();
        System.out.print("Enter approved Event ID: ");
        String id = scanner.nextLine();
        String[] event = system.getEventFileHandler().findEventById(id);
        if (event == null || !event[7].equalsIgnoreCase("APPROVED")) {
            System.out.println("Updates can be published only for approved events.");
            return;
        }
        System.out.print("Update message: ");
        String message = scanner.nextLine();
        EventNotificationManager manager = new EventNotificationManager();
        int count = manager.notifyRegisteredStudents(event[0], event[1], message);
        System.out.println("Observers notified: " + count);
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
