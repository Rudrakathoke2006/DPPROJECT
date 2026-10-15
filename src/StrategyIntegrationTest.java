import strategy.*;import java.util.*;import java.nio.file.*;
public class StrategyIntegrationTest {
 private static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 public static void main(String[] args) throws Exception {
  List<VenueOption> venues=new VenueInventoryRepository().load();
  VenueRequirements r=new VenueRequirements(100,16000,"projector;wifi","2026-11-10");
  VenueSelectionContext ctx=new VenueSelectionContext(new CapacityFirstStrategy());
  List<VenueOption> capacity=ctx.rank(venues,r);
  check(!capacity.isEmpty(),"Should find suitable venues");
  check(capacity.get(0).id.equals("V02"),"Capacity first should prefer auditorium");
  for(VenueOption v:capacity)check(r.rejection(v)==null,"Invalid venue included");
  ctx.setStrategy(new BudgetFirstStrategy());
  List<VenueOption> budget=ctx.rank(venues,r);
  check(budget.get(0).id.equals("V05"),"Budget first should prefer conference room");
  ctx.setStrategy(new EquipmentFirstStrategy());
  List<VenueOption> equipment=ctx.rank(venues,r);
  check(equipment.get(0).id.equals("V02"),"Equipment first should prefer auditorium");
  check(!capacity.get(0).id.equals(budget.get(0).id),"Strategies must be interchangeable");
  check(ctx.rank(venues,new VenueRequirements(10000,100,"projector","2026-11-10")).isEmpty(),"No suitable venue expected");
  VenueOption unavailable=new VenueOption("X","Blocked",200,100,"projector","2026-11-10");
  check(r.rejection(unavailable).contains("unavailable"),"Date constraint not enforced");
  System.out.println("Strategy integration tests PASSED");
 }
}
