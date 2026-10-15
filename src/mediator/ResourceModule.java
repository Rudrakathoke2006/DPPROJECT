package mediator;

/** Adapter over resources.csv. Allocation itself stays in Strategy / Abstract Factory flows. */
final class ResourceModule {
    String[] allocation(String eventId) {
        for (String[] r : CsvRows.read("data/resources.csv", 6)) if (r[0].equalsIgnoreCase(eventId)) return r;
        return null;
    }
}
