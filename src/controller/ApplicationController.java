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

public final class ApplicationController {

    public static void run() {
        Scanner scanner = new Scanner(System.in);
        FestManagementSystem system = FestManagementSystem.getInstance();

        boolean applicationRunning = true;
        while (applicationRunning) {
            view.ConsoleView.println("\n================================================");
            view.ConsoleView.println(" SMART UNIVERSITY EVENT & FEST MANAGEMENT SYSTEM");
            view.ConsoleView.println("================================================");
            view.ConsoleView.println("1. Login");
            view.ConsoleView.println("0. Exit Application");
            view.ConsoleView.print("Enter choice: ");
            String mainChoice = view.ConsoleView.readLine(scanner).trim();

            if (mainChoice.equals("0")) {
                applicationRunning = false;
                continue;
            }
            if (!mainChoice.equals("1")) {
                view.ConsoleView.println("Invalid choice.");
                continue;
            }

            view.ConsoleView.print("Username: ");
            String username = view.ConsoleView.readLine(scanner);
            view.ConsoleView.print("Password: ");
            String password = view.ConsoleView.readLine(scanner);

            User user = system.getAuthenticationManager().login(username, password);
            if (user == null) {
                view.ConsoleView.println("Returning to main menu...");
                continue;
            }

            boolean loggedIn = true;
            while (loggedIn) {
                view.RoleMenuView.showMenu(user.getRole());
                view.ConsoleView.print("Enter choice: ");
                String choice = view.ConsoleView.readLine(scanner);

                if (user.getRole().equals("STUDENT")) {
                    loggedIn = RoleActionController.studentAction(system, user, choice, scanner);
                } else if (user.getRole().equals("FACULTY")) {
                    loggedIn = RoleActionController.facultyAction(system, user, choice, scanner);
                } else if (user.getRole().equals("HOD") || user.getRole().equals("DEAN")) {
                    loggedIn = RoleActionController.authorityAction(system, user, choice, scanner);
                } else if (user.getRole().equals("ADMIN")) {
                    loggedIn = RoleActionController.adminAction(system, user, choice, scanner);
                } else {
                    view.ConsoleView.println("Unsupported role.");
                    loggedIn = false;
                }
            }

            view.ConsoleView.println("Logged out successfully. Returning to main menu...");
        }

        view.ConsoleView.println("Application closed. Thank you.");
        scanner.close();
    }

}
