package mediator;

import observer.EventNotificationManager;

/** Adapter over the existing Observer + Bridge notification path (not re-implemented). */
final class NotificationModule {
    int notifyRegistrants(String eventId, String eventName, String message) {
        return new EventNotificationManager().notifyRegisteredStudents(eventId, eventName, message);
    }
    int historyCount(String eventId) {
        int n = 0;
        for (String[] r : CsvRows.read("data/notifications.csv", 5)) if (r[0].equalsIgnoreCase(eventId)) n++;
        return n;
    }
}
