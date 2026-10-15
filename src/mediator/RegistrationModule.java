package mediator;

import filehandling.RegistrationFileHandler;

/** Adapter over registrations.csv. */
final class RegistrationModule {
    private final RegistrationFileHandler handler = new RegistrationFileHandler();
    boolean isRegistered(String eventId, String student) { return handler.isRegistered(eventId, student); }
    int count(String eventId) {
        int n = 0;
        for (String[] r : CsvRows.read("data/registrations.csv", 5)) if (r[1].equalsIgnoreCase(eventId)) n++;
        return n;
    }
}
