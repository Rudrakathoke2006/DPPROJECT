package resources;

public class TechnicalVenue implements Venue {

    @Override
    public String getVenueName() {
        return "Computer Laboratory";
    }

    @Override
    public void displayDetails() {
        System.out.println("Venue: Computer Laboratory");
    }
}