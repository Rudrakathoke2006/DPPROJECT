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
            view.ConsoleView.println("\nLogin successful!");
            view.ConsoleView.println("Welcome, " + user.getName());
            view.ConsoleView.println("Role: " + user.getRole());

            return user;
        }

        view.ConsoleView.println("\nInvalid username or password.");
        return null;
    }
}