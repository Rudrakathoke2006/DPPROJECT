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

public final class RoleActionController {
    private RoleActionController() {}
    public static boolean studentAction(FestManagementSystem system, User user,
                                         String choice, Scanner scanner) {
        if (choice.equals("0")) return false;
        if (choice.equals("1")) {
            system.getEventService().viewEvents(user);
        } else if (choice.equals("2")) {
            system.getEventService().registerForEvent(user); // Proxy access check
            EventWorkflowController.registerForEvent(system, user, scanner);
        } else if (choice.equals("3")) {
            system.getRegistrationFileHandler().displayStudentRegistrations(user.getUsername());
        } else if (choice.equals("4")) {
            system.getNotificationFileHandler().displayNotificationsFor(user.getUsername());
        } else if (choice.equals("5")) {
            EventWorkflowController.showCoordinationStatus(system, user, scanner);
        } else {
            view.ConsoleView.println("Invalid choice.");
        }
        return true;
    }

    public static boolean facultyAction(FestManagementSystem system, User user,
                                         String choice, Scanner scanner) {
        if (choice.equals("0")) return false;
        if (choice.equals("1")) {
            system.getEventService().viewEvents(user);
        } else if (choice.equals("2")) {
            system.getEventService().createEvent(user); // Proxy access check
            EventWorkflowController.createEvent(system, user, scanner);
        } else if (choice.equals("3")) {
            system.getApprovalManager().showPendingApprovals(user.getUsername(), user.getRole(), scanner);
        } else if (choice.equals("4")) {
            EventWorkflowController.allocateResources(system, scanner);
        } else if (choice.equals("5")) {
            EventWorkflowController.publishUpdate(system, scanner);
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
        } else if (choice.equals("11")) {
            EventWorkflowController.smartVenueSelection(system, user, scanner);
        } else if (choice.equals("12")) {
            new template.EventPreparationService().runConsoleFlow(scanner);
        } else if (choice.equals("13")) {
            EventWorkflowController.showCoordinationStatus(system, user, scanner);
        } else {
            view.ConsoleView.println("Invalid choice.");
        }
        return true;
    }

    public static boolean authorityAction(FestManagementSystem system, User user,
                                           String choice, Scanner scanner) {
        if (choice.equals("0")) return false;
        if (choice.equals("1")) {
            system.getEventService().viewEvents(user);
        } else if (choice.equals("2")) {
            system.getApprovalManager().showPendingApprovals(user.getUsername(), user.getRole(), scanner);
        } else if (choice.equals("3")) {
            EventWorkflowController.publishUpdate(system, scanner);
        } else if (choice.equals("4")) {
            EventWorkflowController.showCoordinationStatus(system, user, scanner);
        } else {
            view.ConsoleView.println("Invalid choice.");
        }
        return true;
    }

    public static boolean adminAction(FestManagementSystem system, User user,
                                       String choice, Scanner scanner) {
        if (choice.equals("0")) return false;
        if (choice.equals("1")) {
            system.getEventService().viewEvents(user);
        } else if (choice.equals("2")) {
            system.getEventService().createEvent(user);
            EventWorkflowController.createEvent(system, user, scanner);
        } else if (choice.equals("3")) {
            EventWorkflowController.allocateResources(system, scanner);
        } else if (choice.equals("4")) {
            EventWorkflowController.publishUpdate(system, scanner);
        } else if (choice.equals("5")) {
            EventWorkflowController.showCoordinationStatus(system, user, scanner);
        } else {
            view.ConsoleView.println("Invalid choice.");
        }
        return true;
    }

}
