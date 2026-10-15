package strategy;
import java.util.*;
public class BudgetFirstStrategy implements VenueSelectionStrategy {
 public String name(){return "Budget First";}
 public Comparator<VenueOption> ranking(VenueRequirements r){return Comparator.comparingDouble((VenueOption v)->v.cost).thenComparingInt(v->v.capacity).thenComparing(v->v.id);}
 public String reason(VenueOption v,VenueRequirements r){return "minimizes booking cost ("+(r.budget-v.cost)+" budget remaining)";}
}
