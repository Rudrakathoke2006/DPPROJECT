package singleton;

import chain.ApprovalManager;
import filehandling.EventFileHandler;
import filehandling.NotificationFileHandler;
import filehandling.RegistrationFileHandler;
import filehandling.ResourceFileHandler;
import proxy.AuthenticationManager;
import proxy.EventService;
import proxy.EventServiceProxy;

public class FestManagementSystem {

    private static FestManagementSystem instance;

    private final AuthenticationManager authenticationManager;
    private final EventService eventService;
    private final EventFileHandler eventFileHandler;
    private final ApprovalManager approvalManager;
    private final ResourceFileHandler resourceFileHandler;
    private final RegistrationFileHandler registrationFileHandler;
    private final NotificationFileHandler notificationFileHandler;

    private FestManagementSystem() {
        authenticationManager = new AuthenticationManager();
        eventService = new EventServiceProxy();
        eventFileHandler = new EventFileHandler();
        approvalManager = new ApprovalManager();
        resourceFileHandler = new ResourceFileHandler();
        registrationFileHandler = new RegistrationFileHandler();
        notificationFileHandler = new NotificationFileHandler();
    }

    public static FestManagementSystem getInstance() {
        if (instance == null) {
            instance = new FestManagementSystem();
        }
        return instance;
    }

    public AuthenticationManager getAuthenticationManager() { return authenticationManager; }
    public EventService getEventService() { return eventService; }
    public EventFileHandler getEventFileHandler() { return eventFileHandler; }
    public ApprovalManager getApprovalManager() { return approvalManager; }
    public ResourceFileHandler getResourceFileHandler() { return resourceFileHandler; }
    public RegistrationFileHandler getRegistrationFileHandler() { return registrationFileHandler; }
    public NotificationFileHandler getNotificationFileHandler() { return notificationFileHandler; }
}
