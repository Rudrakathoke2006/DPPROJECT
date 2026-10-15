package filehandling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import model.User;
import model.Student;
import model.Faculty;
import model.HOD;
import model.Dean;
import model.Admin;

public class UserFileHandler {

    private static final String FILE_PATH = "data/users.csv";

    public User authenticateUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length < 5) continue;

                if (data[2].equals(username) && data[3].equals(password)) {
                    return createUser(data[0], data[1], data[2], data[3], data[4]);
                }
            }
        } catch (IOException e) {
            view.ConsoleView.println("Error reading users file: " + e.getMessage());
        }
        return null;
    }

    public String findUsernameByRole(String role) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 5 && data[4].equalsIgnoreCase(role)) {
                    return data[2];
                }
            }
        } catch (IOException e) {
            view.ConsoleView.println("Error reading users file: " + e.getMessage());
        }
        return null;
    }

    public User findUserByUsername(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 5 && data[2].equals(username)) {
                    return createUser(data[0], data[1], data[2], data[3], data[4]);
                }
            }
        } catch (IOException e) {
            view.ConsoleView.println("Error reading users file: " + e.getMessage());
        }
        return null;
    }

    private User createUser(String userId, String name, String username, String password, String role) {
        switch (role.toUpperCase()) {
            case "STUDENT": return new Student(userId, name, username, password);
            case "FACULTY": return new Faculty(userId, name, username, password);
            case "HOD": return new HOD(userId, name, username, password);
            case "DEAN": return new Dean(userId, name, username, password);
            case "ADMIN": return new Admin(userId, name, username, password);
            default: return null;
        }
    }
}
