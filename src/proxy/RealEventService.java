package proxy;

import filehandling.EventFileHandler;
import model.User;

public class RealEventService implements EventService {

    private final EventFileHandler eventFileHandler = new EventFileHandler();

    @Override
    public void createEvent(User user) {
        view.ConsoleView.println("\n[REAL SERVICE] Event creation access granted to " + user.getName() + ".");
        view.ConsoleView.println("Use the Event Creation module to create and submit the event.");
    }

    @Override
    public void viewEvents(User user) {
        view.ConsoleView.println("\n[REAL SERVICE] Displaying events for " + user.getName() + "...");
        if (user.getRole().equals("STUDENT")) {
            eventFileHandler.displayApprovedEvents();
        } else {
            eventFileHandler.displayAllEvents();
        }
    }

    @Override
    public void registerForEvent(User user) {
        view.ConsoleView.println("\n[REAL SERVICE] Registration access granted to " + user.getName() + ".");
        view.ConsoleView.println("Use the Registration & Notification module to register for an approved event.");
    }

    @Override
    public boolean createAdvancedEvent(User user) {
        view.ConsoleView.println("\n[REAL SERVICE] Advanced event creation access granted to " + user.getName() + ".");
        return true;
    }

    @Override
    public boolean viewCoordinationStatus(User user) {
        view.ConsoleView.println("\n[REAL SERVICE] Coordination status access granted to " + user.getName() + ".");
        return true;
    }

    @Override
    public boolean duplicateEvent(User user) {
        view.ConsoleView.println("\n[REAL SERVICE] Event duplication access granted to " + user.getName() + ".");
        return true;
    }
}
