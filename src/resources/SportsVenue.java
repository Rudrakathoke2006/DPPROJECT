package resources;

public class SportsVenue implements Venue {

    @Override
    public String getVenueName() {
        return "University Sports Ground";
    }

    @Override
    public void displayDetails() {
        System.out.println(
                "Venue: University Sports Ground"
        );
    }
}