package strategy;
import java.util.*;
public final class VenueRequirements {
 public final int attendees;public final double budget;public final Set<String> equipment;public final String date;
 public VenueRequirements(int attendees,double budget,String equipment,String date){if(attendees<=0||budget<0||!Double.isFinite(budget))throw new IllegalArgumentException("Attendees must be positive and budget non-negative.");this.attendees=attendees;this.budget=budget;this.equipment=VenueOption.items(equipment);this.date=date.trim().toLowerCase(Locale.ROOT);}
 public String rejection(VenueOption v){if(v.unavailableDates.contains(date)||v.unavailableDates.contains("all"))return "unavailable on event date";if(v.capacity<attendees)return "insufficient capacity";if(v.cost>budget)return "exceeds budget";if(!v.equipment.containsAll(equipment))return "missing mandatory equipment";return null;}
}
