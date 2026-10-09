import abstractfactory.CulturalResourceFactory;
import abstractfactory.ResourceFactory;
import abstractfactory.SportsResourceFactory;
import abstractfactory.TechnicalResourceFactory;
import abstractfactory.WorkshopResourceFactory;
import filehandling.EventFileHandler;
import filehandling.ResourceFileHandler;
import resources.Equipment;
import resources.SupportStaff;
import resources.Venue;

import java.util.Scanner;

public class Member4Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EventFileHandler eventFileHandler = new EventFileHandler();
        ResourceFileHandler resourceFileHandler = new ResourceFileHandler();

        System.out.println("========================================");
        System.out.println("       RESOURCE ALLOCATION SYSTEM");
        System.out.println("========================================");
        eventFileHandler.displayApprovedEvents();

        System.out.print("\nEnter approved Event ID: ");
        String eventId = scanner.nextLine();
        String[] event = eventFileHandler.findEventById(eventId);

        if (event == null) {
            System.out.println("Event not found.");
            scanner.close();
            return;
        }
        if (!event[7].equalsIgnoreCase("APPROVED")) {
            System.out.println("Resources can be allocated only after final event approval.");
            scanner.close();
            return;
        }
        if (resourceFileHandler.resourceExists(eventId)) {
            System.out.println("Resources are already allocated to this event.");
            scanner.close();
            return;
        }

        String eventName = event[1];
        String eventType = event[2];
        ResourceFactory factory = getFactory(eventType);
        if (factory == null) {
            System.out.println("Unsupported event type: " + eventType);
            scanner.close();
            return;
        }

        Venue venue = factory.createVenue();
        Equipment equipment = factory.createEquipment();
        SupportStaff supportStaff = factory.createSupportStaff();

        System.out.println("\n========================================");
        System.out.println("          ALLOCATED RESOURCES");
        System.out.println("========================================");
        System.out.println("Event: " + eventName + " (" + eventId + ")");
        System.out.println("Type : " + eventType);
        venue.displayDetails();
        equipment.displayDetails();
        supportStaff.displayDetails();

        resourceFileHandler.saveResources(eventId, eventName, eventType,
                venue, equipment, supportStaff);
        scanner.close();
    }

    private static ResourceFactory getFactory(String eventType) {
        if (eventType.equalsIgnoreCase("TECHNICAL")) return new TechnicalResourceFactory();
        if (eventType.equalsIgnoreCase("CULTURAL")) return new CulturalResourceFactory();
        if (eventType.equalsIgnoreCase("SPORTS")) return new SportsResourceFactory();
        if (eventType.equalsIgnoreCase("WORKSHOP")) return new WorkshopResourceFactory();
        return null;
    }
}
