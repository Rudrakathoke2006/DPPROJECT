package model;

import java.util.Locale;

/**
 * Value object holding the OPTIONAL details of an event.
 * Immutable: once created it never changes, so it is safe to share or copy.
 * Shared contract for Builder, Prototype (and later Memento / Strategy).
 */
public class EventSpecification {

    public static final String DEFAULT_DESCRIPTION = "Not provided";
    public static final String DEFAULT_EQUIPMENT = "None";
    public static final String DEFAULT_INSTRUCTIONS = "None";

    private final String description;
    private final double budget;
    private final int capacity;
    private final int durationHours;
    private final String equipment;            // several items separated by ';'
    private final String specialInstructions;

    public EventSpecification(String description, double budget, int capacity,
                              int durationHours, String equipment,
                              String specialInstructions) {

        if (Double.isNaN(budget) || Double.isInfinite(budget) || budget < 0) {
            throw new IllegalArgumentException("Budget must be a non-negative number.");
        }
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative.");
        }
        if (durationHours < 0) {
            throw new IllegalArgumentException("Duration cannot be negative.");
        }

        this.description = textOrDefault(description, DEFAULT_DESCRIPTION);
        this.budget = budget;
        this.capacity = capacity;
        this.durationHours = durationHours;
        this.equipment = textOrDefault(equipment, DEFAULT_EQUIPMENT);
        this.specialInstructions = textOrDefault(specialInstructions, DEFAULT_INSTRUCTIONS);
    }

    /** Specification used when the user skips every optional field. */
    public static EventSpecification defaults() {
        return new EventSpecification(null, 0, 0, 0, null, null);
    }

    /** Exact copy of every field. */
    public EventSpecification copy() {
        return new EventSpecification(description, budget, capacity,
                durationHours, equipment, specialInstructions);
    }

    /**
     * Copy used by Prototype when duplicating an event:
     * reusable fields are kept, event-specific special instructions are reset.
     */
    public EventSpecification copyAsTemplate() {
        return new EventSpecification(description, budget, capacity,
                durationHours, equipment, null);
    }

    public String getDescription() { return description; }
    public double getBudget() { return budget; }
    public int getCapacity() { return capacity; }
    public int getDurationHours() { return durationHours; }
    public String getEquipment() { return equipment; }
    public String getSpecialInstructions() { return specialInstructions; }

    public void display() {
        System.out.println("Description         : " + description);
        System.out.println("Budget              : " + String.format(Locale.US, "%.2f", budget));
        System.out.println("Expected capacity   : " + capacity);
        System.out.println("Duration (hours)    : " + durationHours);
        System.out.println("Equipment needs     : " + equipment);
        System.out.println("Special instructions: " + specialInstructions);
    }

    /** CSV-safe text: no commas / line breaks (the project's CSV has no quoting). */
    public static String sanitize(String value) {
        return value == null ? ""
                : value.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private static String textOrDefault(String value, String defaultValue) {
        String clean = sanitize(value);
        return clean.isEmpty() ? defaultValue : clean;
    }
}