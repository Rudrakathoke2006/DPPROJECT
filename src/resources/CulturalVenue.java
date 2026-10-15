package resources;

public class CulturalVenue implements Venue {

    @Override
    public String getVenueName() {
        return "University Auditorium";
    }

    @Override
    public void displayDetails() {
        view.ConsoleView.println(
                "Venue: University Auditorium"
        );
    }
}