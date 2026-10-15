import builder.*;
import factory.*;
import filehandling.*;
import memento.*;
import model.*;
import prototype.*;
import strategy.*;
import java.util.*;
import java.nio.file.*;

/** Run only from an isolated scratch copy. Modifies data CSV files. */
public class FullRegressionTest {
 static int checks=0;
 static void ok(boolean b,String msg){checks++;if(!b)throw new AssertionError(msg);}
 static String row(String file,String id)throws Exception {for(String s:Files.readAllLines(Paths.get("data/"+file)))if(s.startsWith(id+","))return s;return null;}
 public static void main(String[]args)throws Exception {
  Faculty faculty=new Faculty("F101","Dr Rahul Mehta","rahul","x");
  Student student=new Student("S101","Student","student","x");
  AdvancedEventCreationService service=new AdvancedEventCreationService();
  Event event=new EventBuilder(new TechnicalEventCreator()).withEventId("QA900").withName("Before").withOrganizer(faculty.getName()).withDate("10-11-2026").withVenue("Lab").withLevel("DEPARTMENT").withBudget(5000).withCapacity(60).build();
  ok(service.submitNewEvent(event),"create");
  ok(!service.submitNewEvent(event),"duplicate rejected");
  ok(row("approvals.csv","QA900").contains("PENDING"),"approval created");
  Event clone=new CloneEventService().prepareClone("QA900","QA901","Clone",faculty.getName(),"12-11-2026","Hall");
  ok(service.submitNewEvent(clone),"clone saved");
  ok(row("events.csv","QA901").endsWith("PENDING"),"clone pending");
  ok(row("event_details.csv","QA901")!=null,"clone details");
  EventEditService edit=new EventEditService();
  ok(!edit.edit(student,new Scanner("")),"student denied edit");
  ok(edit.edit(faculty,new Scanner("QA900\nAfter\n\n\n\n\n\n\n\n\n")),"edit");
  ok(row("events.csv","QA900").contains("After"),"edit persisted");
  ok(edit.edit(faculty,new Scanner("QA900\nAgain\n\n\n\n\n\n\n\n\n")),"second edit");
  ok(edit.undoLast(faculty,new Scanner("QA900\n")),"undo first");
  ok(row("events.csv","QA900").contains("After"),"undo first state");
  ok(edit.undoLast(faculty,new Scanner("QA900\n")),"undo second");
  ok(row("events.csv","QA900").contains("Before"),"undo second state");
  ok(!edit.undoLast(faculty,new Scanner("QA900\n")),"undo exhaustion");
  ok(row("approvals.csv","QA900").contains("PENDING"),"reapproval pending");
  ok(new EventHistoryCaretaker().history("QA900").size()>=6,"persistent history");
  List<VenueOption> venues=new VenueInventoryRepository().load();
  VenueRequirements req=new VenueRequirements(100,16000,"projector;wifi","2026-11-10");
  VenueSelectionContext ctx=new VenueSelectionContext(new CapacityFirstStrategy());
  List<VenueOption> first=ctx.rank(venues,req);ok(!first.isEmpty(),"strategy candidates");
  ctx.setStrategy(new BudgetFirstStrategy());ok(!first.get(0).id.equals(ctx.rank(venues,req).get(0).id),"strategy switch");
  ok(ctx.rank(venues,new VenueRequirements(999999,0,"","2026-11-10")).isEmpty(),"infeasible empty");
  System.out.println("FULL REGRESSION PASSED: "+checks+" assertions");
 }
}
