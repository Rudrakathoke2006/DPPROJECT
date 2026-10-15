package strategy;
import abstractfactory.ResourceFactory;import filehandling.*;import model.User;import resources.*;import java.io.*;import java.util.*;
/** Strategy ranks inventory; existing Abstract Factory still supplies category equipment/staff. */
public final class SmartVenueSelectionService {
 private boolean bookedOnSameDate(String venueName,String date,String currentId) {
  try (BufferedReader reader=new BufferedReader(new FileReader("data/resources.csv"))) {
   reader.readLine(); String line;
   EventFileHandler events=new EventFileHandler();
   while((line=reader.readLine())!=null){
    String[] a=line.split(",",-1);
    if(a.length<6 || a[0].equalsIgnoreCase(currentId) || !a[3].equalsIgnoreCase(venueName))continue;
    String[] other=events.findEventById(a[0]);
    if(other!=null && !"REJECTED".equalsIgnoreCase(other[7]) && other[5].equalsIgnoreCase(date))return true;
   }
  }catch(IOException e){throw new IllegalStateException("Cannot verify existing venue bookings: "+e.getMessage(),e);}
  return false;
 }
 public void runConsoleFlow(User user,Scanner scanner,ResourceFactory factory,String id){
  if(user==null||!("FACULTY".equalsIgnoreCase(user.getRole())||"ADMIN".equalsIgnoreCase(user.getRole()))){view.ConsoleView.println("Access denied.");return;}
  String[] event=new EventFileHandler().findEventById(id);
  if(event==null||!"APPROVED".equalsIgnoreCase(event[7])){view.ConsoleView.println("Only approved events can receive final venue allocations.");return;}
  ResourceFileHandler resources=new ResourceFileHandler();if(resources.resourceExists(id)){view.ConsoleView.println("This event already has allocated resources.");return;}
  if(factory==null){view.ConsoleView.println("No Abstract Factory for this event category.");return;}
  try{
   view.ConsoleView.print("Expected attendees (positive integer): ");int attendees=Integer.parseInt(view.ConsoleView.readLine(scanner).trim());
   view.ConsoleView.print("Maximum venue booking budget: ");double budget=Double.parseDouble(view.ConsoleView.readLine(scanner).trim());
   view.ConsoleView.print("Mandatory equipment (semicolon-separated, blank for none): ");String equipment=view.ConsoleView.readLine(scanner);
   VenueRequirements requirements=new VenueRequirements(attendees,budget,equipment,event[5]);
   view.ConsoleView.println("1. Capacity First  2. Budget First  3. Equipment First");view.ConsoleView.print("Choose ranking strategy: ");String choice=view.ConsoleView.readLine(scanner).trim();
   VenueSelectionStrategy algorithm;switch(choice){case "1":algorithm=new CapacityFirstStrategy();break;case "2":algorithm=new BudgetFirstStrategy();break;case "3":algorithm=new EquipmentFirstStrategy();break;default:view.ConsoleView.println("Invalid strategy.");return;}
   List<VenueOption> inventory=new VenueInventoryRepository().load();
   inventory.removeIf(v -> bookedOnSameDate(v.name, event[5], id));
   VenueSelectionContext context=new VenueSelectionContext(algorithm);List<VenueOption> ranked=context.rank(inventory,requirements);
   view.ConsoleView.println("\nRanking: "+context.strategyName());for(VenueOption v:inventory){String reason=requirements.rejection(v);if(reason!=null)view.ConsoleView.println("Excluded "+v.name+": "+reason);}
   if(ranked.isEmpty()){view.ConsoleView.println("No qualifying venues. No allocation saved.");return;}
   for(int i=0;i<ranked.size();i++){VenueOption v=ranked.get(i);view.ConsoleView.println((i+1)+". "+v+" | "+context.reason(v,requirements));}
   view.ConsoleView.println("Recommended: "+ranked.get(0).name);view.ConsoleView.print("Choose ranked venue number (0 cancels): ");int selection=Integer.parseInt(view.ConsoleView.readLine(scanner).trim());if(selection==0){view.ConsoleView.println("Cancelled; nothing allocated.");return;}if(selection<1||selection>ranked.size()){view.ConsoleView.println("Invalid selection.");return;}
   VenueOption selected=ranked.get(selection-1);Equipment categoryEquipment=factory.createEquipment();SupportStaff staff=factory.createSupportStaff();
   Venue allocatedVenue=new Venue(){public String getVenueName(){return selected.name;}public void displayDetails(){view.ConsoleView.println("Selected venue: "+selected.name);}};
   if(resources.saveResources(id,event[1],event[2],allocatedVenue,categoryEquipment,staff)){view.ConsoleView.println("Venue allocated using Strategy + existing Abstract Factory. Proposed venue remains unchanged.");new mediator.UniversityEventCoordinator().resourcesAllocated(id,selected.name);}
  }catch(NumberFormatException e){view.ConsoleView.println("Invalid numeric input. No allocation saved.");}catch(IllegalArgumentException|IOException|IllegalStateException e){view.ConsoleView.println("Venue selection failed: "+e.getMessage());}
 }
}
