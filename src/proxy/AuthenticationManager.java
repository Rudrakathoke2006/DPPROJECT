package proxy;

import filehandling.UserFileHandler;
import model.User;

public class AuthenticationManager {

    private UserFileHandler userFileHandler;

    public AuthenticationManager() {
        userFileHandler = new UserFileHandler();
    }

    public User login(String username, String password) {

        User user =
                userFileHandler.authenticateUser(username, password);

        if (user != null) {
            System.out.println("\nLogin successful!");
            System.out.println("Welcome, " + user.getName());
            System.out.println("Role: " + user.getRole());

            return user;
        }

        System.out.println("\nInvalid username or password.");
        return null;
    }
}