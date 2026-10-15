import builder.AdvancedEventCreationService;
import builder.EventBuilder;
import factory.*;
import filehandling.EventDetailsFileHandler;
import filehandling.EventFileHandler;
import model.Event;
import model.EventSpecification;
import prototype.CloneEventService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Member1BuilderPrototypeTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        AdvancedEventCreationService service = new AdvancedEventCreationService();
        EventFileHandler events = new EventFileHandler();
        EventDetailsFileHandler details = new EventDetailsFileHandler();
        CloneEventService cloneService = new CloneEventService();

        // ---------- BUILDER ----------
        Event minimal = new EventBuilder(new TechnicalEventCreator())
                .withEventId("B001").withName("Minimal").withOrganizer("Dr Rahul Mehta")
                .withDate("10-05-2026").withVenue("Lab1").withLevel("DEPARTMENT").build();
        check("B1 minimal build succeeds with defaults",
                minimal.getSpecification().getBudget() == 0
                        && minimal.getSpecification().getDescription().equals(EventSpecification.DEFAULT_DESCRIPTION)
                        && minimal.getSpecification().getEquipment().equals("None"));

        details.detailsExist("INIT");   // makes sure event_details.csv exists before counting lines
        int eventsBefore = lines("data/events.csv");
        int detailsBefore = lines("data/event_details.csv");
        int approvalsBefore = lines("data/approvals.csv");

        Event adv = new EventBuilder(new TechnicalEventCreator())
                .withEventId("B002").withName("Hackathon").withOrganizer("Dr Rahul Mehta")
                .withDate("20-06-2026").withVenue("Auditorium").withLevel("UNIVERSITY")
                .withDescription("24 hour coding").withBudget(50000.5).withCapacity(120)
                .withDuration(24).withEquipment("Projector;Laptops").withSpecialInstructions("Bring ID")
                .build();
        check("B2a advanced event saved", service.submitNewEvent(adv));
        // new handler instances = what an application restart would see
        EventSpecification reloaded = new EventDetailsFileHandler().findDetails("B002");
        check("B2b optional fields survive restart",
                reloaded.getDescription().equals("24 hour coding")
                        && reloaded.getBudget() == 50000.5 && reloaded.getCapacity() == 120
                        && reloaded.getDurationHours() == 24
                        && reloaded.getEquipment().equals("Projector;Laptops")
                        && reloaded.getSpecialInstructions().equals("Bring ID"));
        check("B2c saved as PENDING with 1 event row, 1 details row, 1 approval row",
                events.findEventById("B002")[7].equals("PENDING")
                        && lines("data/events.csv") == eventsBefore + 1
                        && lines("data/event_details.csv") == detailsBefore + 1
                        && lines("data/approvals.csv") == approvalsBefore + 1);

        eventsBefore = lines("data/events.csv");
        detailsBefore = lines("data/event_details.csv");
        approvalsBefore = lines("data/approvals.csv");
        check("B3a negative budget rejected", throwsOnBuild(new EventBuilder(new SportsEventCreator())
                .withEventId("B003").withName("X").withOrganizer("O").withDate("10-05-2026")
                .withVenue("V").withLevel("DEPARTMENT").withBudget(-5)));
        check("B3b negative capacity rejected", throwsOnBuild(new EventBuilder(new SportsEventCreator())
                .withEventId("B003").withName("X").withOrganizer("O").withDate("10-05-2026")
                .withVenue("V").withLevel("DEPARTMENT").withCapacity(-1)));
        check("B3c empty name rejected", throwsOnBuild(new EventBuilder(new SportsEventCreator())
                .withEventId("B003").withName("  ").withOrganizer("O").withDate("10-05-2026")
                .withVenue("V").withLevel("DEPARTMENT")));
        check("B3d empty id rejected", throwsOnBuild(new EventBuilder(new SportsEventCreator())
                .withEventId("").withName("X").withOrganizer("O").withDate("10-05-2026")
                .withVenue("V").withLevel("DEPARTMENT")));
        check("B3e bad date rejected", throwsOnBuild(new EventBuilder(new SportsEventCreator())
                .withEventId("B003").withName("X").withOrganizer("O").withDate("tomorrow")
                .withVenue("V").withLevel("DEPARTMENT")));
        check("B3f bad level rejected", throwsOnBuild(new EventBuilder(new SportsEventCreator())
                .withEventId("B003").withName("X").withOrganizer("O").withDate("10-05-2026")
                .withVenue("V").withLevel("GLOBAL")));
        check("B3g no files changed by rejected builds",
                lines("data/events.csv") == eventsBefore
                        && lines("data/event_details.csv") == detailsBefore
                        && lines("data/approvals.csv") == approvalsBefore);

        String[] ids = {"B101", "B102", "B103", "B104"};
        EventCreator[] creators = {new TechnicalEventCreator(), new CulturalEventCreator(),
                new SportsEventCreator(), new WorkshopCreator()};
        String[] types = {"TECHNICAL", "CULTURAL", "SPORTS", "WORKSHOP"};
        Class<?>[] classes = {TechnicalEvent.class, CulturalEvent.class, SportsEvent.class, Workshop.class};
        for (int i = 0; i < 4; i++) {
            Event e = new EventBuilder(creators[i]).withEventId(ids[i]).withName("T" + i)
                    .withOrganizer("O").withDate("10-05-2026").withVenue("V").withLevel("DEPARTMENT").build();
            check("B4 " + types[i] + " built through its existing creator",
                    classes[i].isInstance(e) && e.getEventType().equals(types[i]));
        }

        // ---------- PROTOTYPE ----------
        Event source = new EventBuilder(new CulturalEventCreator())
                .withEventId("P000").withName("Source").withOrganizer("Dr Rahul Mehta")
                .withDate("01-07-2026").withVenue("Hall").withLevel("INTER_DEPARTMENT")
                .withDescription("Fest night").withBudget(9000).withCapacity(300).withDuration(5)
                .withEquipment("Sound;Lights").withSpecialInstructions("Gate 2 only").build();
        service.submitNewEvent(source);
        events.updateEventStatus("P000", "APPROVED");

        int regBefore = lines("data/registrations.csv");
        int resBefore = lines("data/resources.csv");
        int notBefore = lines("data/notifications.csv");
        approvalsBefore = lines("data/approvals.csv");

        Event clone = cloneService.prepareClone("P000", "P001", "Source Copy", "Dr Rahul Mehta", "09-08-2026", "");
        check("P1 clone has different ID and keeps concrete type",
                !clone.getEventId().equals("P000") && clone instanceof CulturalEvent
                        && clone.getEventType().equals("CULTURAL") && clone.getEventLevel().equals("INTER_DEPARTMENT"));
        check("P2a template fields copied",
                clone.getSpecification().getDescription().equals("Fest night")
                        && clone.getSpecification().getBudget() == 9000
                        && clone.getSpecification().getCapacity() == 300
                        && clone.getSpecification().getEquipment().equals("Sound;Lights")
                        && clone.getVenue().equals("Hall"));
        check("P2b special instructions NOT copied",
                clone.getSpecification().getSpecialInstructions().equals("None"));
        check("P3 clone starts PENDING although source APPROVED", clone.getStatus().equals("PENDING"));

        check("P4a clone saved", service.submitNewEvent(clone));
        check("P4b source row untouched and still APPROVED",
                events.findEventById("P000")[7].equals("APPROVED")
                        && events.findEventById("P000")[1].equals("Source")
                        && events.findEventById("P000")[5].equals("01-07-2026"));
        check("P4c one new approval row, no registration/resource/notification rows",
                lines("data/approvals.csv") == approvalsBefore + 1
                        && lines("data/registrations.csv") == regBefore
                        && lines("data/resources.csv") == resBefore
                        && lines("data/notifications.csv") == notBefore);
        check("P4d clone details persisted separately from source",
                new EventDetailsFileHandler().detailsExist("P001")
                        && new EventDetailsFileHandler().findDetails("P000").getSpecialInstructions().equals("Gate 2 only"));

        // independent mutable fields (in memory)
        Event src = new prototype.EventCloner().loadEvent("P000");
        Event c2 = src.copyEventTemplate("P002", "C2", "O", "01-01-2027", "V2");
        c2.setSpecification(new EventSpecification("changed", 1, 1, 1, "x", "y"));
        c2.setStatus("REJECTED");
        check("P5 editing clone does not modify source",
                src.getSpecification().getDescription().equals("Fest night") && src.getStatus().equals("APPROVED"));

        events.updateEventStatus("P000", "REJECTED");
        Event c3 = cloneService.prepareClone("P000", "P003", "", "O", "02-02-2027", "New Venue");
        check("P6 clone PENDING even if source REJECTED; name/venue overrides applied",
                c3.getStatus().equals("PENDING") && c3.getEventName().equals("Source (Copy)")
                        && c3.getVenue().equals("New Venue"));

        check("P7a duplicate destination ID blocked in prepareClone",
                throwsOnClone(cloneService, "P000", "P001"));
        Event duplicateId = new EventBuilder(new CulturalEventCreator()).withEventId("P001").withName("dup")
                .withOrganizer("O").withDate("10-05-2026").withVenue("V").withLevel("DEPARTMENT").build();
        check("P7b duplicate destination ID blocked in submit", !service.submitNewEvent(duplicateId));
        check("P7c missing source blocked", throwsOnClone(cloneService, "NOPE", "P009"));

        System.out.println("\nPassed: " + passed + "   Failed: " + failed);
        if (failed > 0) System.exit(1);
    }

    private static boolean throwsOnBuild(EventBuilder b) {
        try { b.build(); return false; } catch (IllegalArgumentException e) { return true; }
    }

    private static boolean throwsOnClone(CloneEventService s, String src, String dst) {
        try { s.prepareClone(src, dst, "n", "o", "10-05-2026", "v"); return false; }
        catch (IllegalArgumentException e) { return true; }
    }

    private static int lines(String path) {
        int n = 0;
        try (BufferedReader r = new BufferedReader(new FileReader(path))) {
            while (r.readLine() != null) n++;
        } catch (IOException e) { return 0; }
        return n;
    }

    private static void check(String name, boolean ok) {
        if (ok) { passed++; System.out.println("PASS  " + name); }
        else { failed++; System.out.println("FAIL  " + name); }
    }
}
