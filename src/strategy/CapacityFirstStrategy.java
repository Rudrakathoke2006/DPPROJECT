package strategy;
import java.util.*;
public class CapacityFirstStrategy implements VenueSelectionStrategy {
 public String name(){return "Capacity First";}
 public Comparator<VenueOption> ranking(VenueRequirements r){return Comparator.comparingInt((VenueOption v)->v.capacity).reversed().thenComparingDouble(v->v.cost).thenComparing(v->v.id);}
 public String reason(VenueOption v,VenueRequirements r){return "maximizes available seating ("+(v.capacity-r.attendees)+" spare seats)";}
}
