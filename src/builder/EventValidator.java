package builder;

/**
 * Validation of the MANDATORY event fields.
 * Shared by the Builder (advanced create) and the Prototype (duplicate event).
 */
public final class EventValidator {

    private EventValidator() { }

    public static void validateCore(String eventId, String eventName, String organizer,
                                    String date, String venue, String level) {

        if (isBlank(eventId)) {
            throw new IllegalArgumentException("Event ID is required.");
        }
        if (!eventId.trim().matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException(
                    "Event ID may contain only letters, digits, '-' and '_'.");
        }
        if (isBlank(eventName)) {
            throw new IllegalArgumentException("Event name is required.");
        }
        if (isBlank(organizer)) {
            throw new IllegalArgumentException("Organizer is required.");
        }
        if (isBlank(date)) {
            throw new IllegalArgumentException("Date is required.");
        }
        if (!date.trim().matches("\\d{1,4}[-/.]\\d{1,2}[-/.]\\d{1,4}")) {
            throw new IllegalArgumentException(
                    "Date format is invalid (example: 15-03-2026).");
        }
        if (isBlank(venue)) {
            throw new IllegalArgumentException("Proposed venue is required.");
        }
        if (!isValidLevel(level)) {
            throw new IllegalArgumentException(
                    "Event level must be DEPARTMENT, INTER_DEPARTMENT or UNIVERSITY.");
        }
    }

    public static boolean isValidLevel(String level) {
        return level != null
                && (level.equals("DEPARTMENT")
                || level.equals("INTER_DEPARTMENT")
                || level.equals("UNIVERSITY"));
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
