package proxy;

import model.User;

public class EventServiceProxy implements EventService {

    private final RealEventService realEventService;

    public EventServiceProxy() {
        realEventService = new RealEventService();
    }

    @Override
    public void createEvent(User user) {
        if (!isAuthenticated(user)) return;
        if (user.getRole().equals("FACULTY") || user.getRole().equals("ADMIN")) {
            realEventService.createEvent(user);
        } else {
            view.ConsoleView.println("Access denied: Only Faculty/Admin can create events.");
        }
    }

    @Override
    public void viewEvents(User user) {
        if (!isAuthenticated(user)) return;
        realEventService.viewEvents(user);
    }

    @Override
    public void registerForEvent(User user) {
        if (!isAuthenticated(user)) return;
        if (user.getRole().equals("STUDENT")) {
            realEventService.registerForEvent(user);
        } else {
            view.ConsoleView.println("Access denied: Only students can register for events.");
        }
    }
        @Override
    public boolean createAdvancedEvent(User user) {
        if (!isAuthenticated(user)) return false;
        if (user.getRole().equals("FACULTY")) {
            return realEventService.createAdvancedEvent(user);
        }
        view.ConsoleView.println("Access denied: Only Faculty can create advanced events.");
        return false;
    }

    @Override
    public boolean duplicateEvent(User user) {
        if (!isAuthenticated(user)) return false;
        if (user.getRole().equals("FACULTY")) {
            return realEventService.duplicateEvent(user);
        }
        view.ConsoleView.println("Access denied: Only Faculty can duplicate events.");
        return false;
    }

    @Override
    public boolean viewCoordinationStatus(User user) {
        if (!isAuthenticated(user)) return false;
        return realEventService.viewCoordinationStatus(user);
    }

    private boolean isAuthenticated(User user) {
        if (user == null) {
            view.ConsoleView.println("Access denied: Please login first.");
            return false;
        }
        return true;
    }
}
