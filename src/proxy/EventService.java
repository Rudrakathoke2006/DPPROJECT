package proxy;

import model.User;

public interface EventService {

    void createEvent(User user);

    void viewEvents(User user);

    void registerForEvent(User user);

    // Builder / Prototype entry points: return true only if access is granted
    boolean createAdvancedEvent(User user);

    boolean duplicateEvent(User user);
}