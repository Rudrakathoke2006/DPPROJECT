
import model.User;
import proxy.AuthenticationManager;
import proxy.EventService;
import proxy.EventServiceProxy;

import java.util.Scanner;

public class Member1Test {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        AuthenticationManager authenticationManager =
                new AuthenticationManager();

        System.out.println("=================================");
        System.out.println(" UNIVERSITY EVENT MANAGEMENT");
        System.out.println("=================================");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        User loggedInUser =
                authenticationManager.login(username, password);

        if (loggedInUser == null) {
            System.out.println("Login failed.");
            return;
        }

        EventService eventService =
                new EventServiceProxy();

        System.out.println("\nTesting access...");

        eventService.viewEvents(loggedInUser);

        eventService.createEvent(loggedInUser);

        eventService.registerForEvent(loggedInUser);

        scanner.close();
    }
}