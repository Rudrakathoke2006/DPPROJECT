package strategy;
import java.util.*;
public class EquipmentFirstStrategy implements VenueSelectionStrategy {
 public String name(){return "Equipment First";}
 public Comparator<VenueOption> ranking(VenueRequirements r){return Comparator.comparingInt((VenueOption v)->v.extraEquipment(r.equipment)).reversed().thenComparingDouble(v->v.cost).thenComparing(v->v.id);}
 public String reason(VenueOption v,VenueRequirements r){return "offers "+v.extraEquipment(r.equipment)+" additional equipment options";}
}
