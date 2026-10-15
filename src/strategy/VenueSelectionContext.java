package strategy;
import java.util.*;
public final class VenueSelectionContext {
 private VenueSelectionStrategy strategy;
 public VenueSelectionContext(VenueSelectionStrategy strategy){setStrategy(strategy);}
 public void setStrategy(VenueSelectionStrategy strategy){this.strategy=Objects.requireNonNull(strategy);}
 public String strategyName(){return strategy.name();}
 public String reason(VenueOption v,VenueRequirements r){return strategy.reason(v,r);}
 public List<VenueOption> rank(List<VenueOption> inventory,VenueRequirements r){List<VenueOption> valid=new ArrayList<>();for(VenueOption v:inventory)if(r.rejection(v)==null)valid.add(v);valid.sort(strategy.ranking(r));return valid;}
}
