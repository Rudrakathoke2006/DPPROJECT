import builder.*;import factory.*;import filehandling.*;import model.*;import strategy.*;import java.util.*;
public class StrategyConsoleRegressionTest {
 static void check(boolean b,String s){if(!b)throw new AssertionError(s);}
 public static void main(String[]args)throws Exception {
  Faculty f=new Faculty("F101","Dr Rahul Mehta","rahul","x");
  Event e=new EventBuilder(new TechnicalEventCreator()).withEventId("QA950").withName("Venue test").withOrganizer(f.getName()).withDate("10-11-2026").withVenue("Proposed hall").withLevel("DEPARTMENT").build();
  check(new AdvancedEventCreationService().submitNewEvent(e),"create");
  SmartVenueSelectionService svc=new SmartVenueSelectionService();
  svc.runConsoleFlow(f,new Scanner("100\n16000\nprojector;wifi\n2\n1\n"),new abstractfactory.TechnicalResourceFactory(),"QA950");
  check(!new ResourceFileHandler().resourceExists("QA950"),"pending must not allocate");
  new EventFileHandler().updateEventStatus("QA950","APPROVED");
  svc.runConsoleFlow(f,new Scanner("100\n16000\nprojector;wifi\n2\n0\n"),new abstractfactory.TechnicalResourceFactory(),"QA950");
  check(!new ResourceFileHandler().resourceExists("QA950"),"cancel no write");
  svc.runConsoleFlow(f,new Scanner("100\n16000\nprojector;wifi\n2\n1\n"),new abstractfactory.TechnicalResourceFactory(),"QA950");
  check(new ResourceFileHandler().resourceExists("QA950"),"approved allocation");
  check(new EventFileHandler().findEventById("QA950")[6].equals("Proposed hall"),"proposed venue unchanged");
  check(!new memento.EventEditService().edit(f,new Scanner("QA950\n")),"allocated event edit blocked");
  System.out.println("STRATEGY CONSOLE REGRESSION PASSED");
 }
}
