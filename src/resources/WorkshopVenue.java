package resources;

public class WorkshopVenue implements Venue {

    @Override
    public String getVenueName() {
        return "Seminar Hall";
    }

    @Override
    public void displayDetails() {
        System.out.println(
                "Venue: Seminar Hall"
        );
    }
}