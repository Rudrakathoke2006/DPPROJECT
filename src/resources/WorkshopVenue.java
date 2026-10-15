package resources;

public class WorkshopVenue implements Venue {

    @Override
    public String getVenueName() {
        return "Seminar Hall";
    }

    @Override
    public void displayDetails() {
        view.ConsoleView.println(
                "Venue: Seminar Hall"
        );
    }
}