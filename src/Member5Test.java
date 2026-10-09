import filehandling.EventFileHandler;
import filehandling.NotificationFileHandler;
import filehandling.RegistrationFileHandler;
import model.User;
import observer.EventNotificationManager;
import proxy.AuthenticationManager;

import java.util.Scanner;

public class Member5Test {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthenticationManager authenticationManager = new AuthenticationManager();

        System.out.println("========================================");
        System.out.println(" REGISTRATION & EVENT NOTIFICATION SYSTEM");
        System.out.println("========================================");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = authenticationManager.login(username, password);
        if (user == null) {
            scanner.close();
            return;
        }

        if (user.getRole().equals("STUDENT")) {
            studentMenu(user, scanner);
        } else if (user.getRole().equals("FACULTY") || user.getRole().equals("HOD")
                || user.getRole().equals("DEAN") || user.getRole().equals("ADMIN")) {
            publisherMenu(user, scanner);
        } else {
            System.out.println("This role cannot use the notification module.");
        }
        scanner.close();
    }

    private static void studentMenu(User user, Scanner scanner) {
        EventFileHandler eventHandler = new EventFileHandler();
        RegistrationFileHandler registrationHandler = new RegistrationFileHandler();
        NotificationFileHandler notificationHandler = new NotificationFileHandler();

        System.out.println("\n1. Register for an approved event");
        System.out.println("2. View my registrations");
        System.out.println("3. View my notification history");
        System.out.print("Enter choice: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            eventHandler.displayApprovedEvents();
            System.out.print("\nEnter Event ID: ");
            String eventId = scanner.nextLine();
            String[] event = eventHandler.findEventById(eventId);
            if (event == null || !event[7].equalsIgnoreCase("APPROVED")) {
                System.out.println("Only approved events are open for registration.");
                return;
            }

            System.out.print("Notification channel (EMAIL/SMS/INAPP): ");
            String channel = scanner.nextLine().toUpperCase();
            if (!validChannel(channel)) {
                System.out.println("Invalid notification channel.");
                return;
            }
            registrationHandler.registerStudent(event[0], event[1], user.getUsername(), channel);

        } else if (choice.equals("2")) {
            registrationHandler.displayStudentRegistrations(user.getUsername());
        } else if (choice.equals("3")) {
            notificationHandler.displayNotificationsFor(user.getUsername());
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void publisherMenu(User user, Scanner scanner) {
        EventFileHandler eventHandler = new EventFileHandler();
        eventHandler.displayApprovedEvents();
        System.out.print("\nEnter Event ID to publish an update: ");
        String eventId = scanner.nextLine();
        String[] event = eventHandler.findEventById(eventId);

        if (event == null || !event[7].equalsIgnoreCase("APPROVED")) {
            System.out.println("Updates can be published only for approved events.");
            return;
        }

        System.out.print("Enter update message: ");
        String message = scanner.nextLine();
        EventNotificationManager manager = new EventNotificationManager();
        int notified = manager.notifyRegisteredStudents(event[0], event[1], message);
        System.out.println("Notification process completed. Observers notified: " + notified);
    }

    private static boolean validChannel(String channel) {
        return channel.equals("EMAIL") || channel.equals("SMS") || channel.equals("INAPP");
    }
}
