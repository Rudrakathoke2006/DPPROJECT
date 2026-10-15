package campus.web;

import java.util.*;
import model.User;
import strategy.*;
import abstractfactory.*;
import filehandling.EventFileHandler;

/** Converts a venue-card selection to the existing SmartVenueSelectionService's ranked choice. */
final class VenueAdapter {
    static void allocate(User user, String id, String venueId, int attendees, double budget, String equipment) throws Exception {
        String[] event = new EventFileHandler().findEventById(id);
        if(event == null || !"APPROVED".equals(event[7])) throw new IllegalArgumentException("Choose an approved event.");
        VenueRequirements requirements = new VenueRequirements(attendees,budget,equipment,event[5]);
        List<VenueOption> inventory = new VenueInventoryRepository().load();
        List<Map<String,String>> allocations = ViewData.rows("resources");
        for(Map<String,String> allocation:allocations) {
            if(id.equalsIgnoreCase(allocation.get("eventId"))) throw new IllegalArgumentException("This event already has resources allocated.");
            String[] other = new EventFileHandler().findEventById(allocation.get("eventId"));
            if(other != null && !"REJECTED".equalsIgnoreCase(other[7]) && event[5].equalsIgnoreCase(other[5]))
                inventory.removeIf(v -> v.name.equalsIgnoreCase(allocation.get("venue")));
        }
        VenueOption chosen = inventory.stream().filter(v->v.id.equals(venueId)).findFirst().orElseThrow(()->new IllegalArgumentException("This venue is unavailable or already booked on the event date."));
        String rejection = requirements.rejection(chosen);
        if(rejection != null) throw new IllegalArgumentException("Cannot allocate " + chosen.name + ": " + rejection + ".");
        List<VenueOption> ranked = new VenueSelectionContext(new CapacityFirstStrategy()).rank(inventory,requirements);
        int selection = ranked.indexOf(chosen)+1;
        ResourceFactory factory;
        switch(event[2]) {
            case "TECHNICAL": factory=new TechnicalResourceFactory();break;
            case "CULTURAL": factory=new CulturalResourceFactory();break;
            case "SPORTS": factory=new SportsResourceFactory();break;
            case "WORKSHOP": factory=new WorkshopResourceFactory();break;
            default:throw new IllegalArgumentException("Unsupported event category.");
        }
        try(Scanner scanner=new Scanner(attendees+"\n"+budget+"\n"+equipment+"\n1\n"+selection+"\n")) {
            new SmartVenueSelectionService().runConsoleFlow(user,scanner,factory,id);
        }
        if(!new filehandling.ResourceFileHandler().resourceExists(id)) throw new IllegalArgumentException("Allocation could not be saved. Please try again.");
    }
}
