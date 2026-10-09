package factory;

/**
 * Small helper that picks the existing Factory Method creator.
 * Used by the Builder (menu choice) and the Prototype loader (type text from CSV),
 * so the "which creator?" decision is not copy-pasted again.
 */
public final class EventCreatorProvider {

    private EventCreatorProvider() { }

    public static EventCreator forChoice(String choice) {
        if (choice == null) return null;
        choice = choice.trim();
        if (choice.equals("1")) return new TechnicalEventCreator();
        if (choice.equals("2")) return new CulturalEventCreator();
        if (choice.equals("3")) return new SportsEventCreator();
        if (choice.equals("4")) return new WorkshopCreator();
        return null;
    }

    public static EventCreator forType(String type) {
        if (type == null) return null;
        if (type.equalsIgnoreCase("TECHNICAL")) return new TechnicalEventCreator();
        if (type.equalsIgnoreCase("CULTURAL")) return new CulturalEventCreator();
        if (type.equalsIgnoreCase("SPORTS")) return new SportsEventCreator();
        if (type.equalsIgnoreCase("WORKSHOP")) return new WorkshopCreator();
        return null;
    }
}
