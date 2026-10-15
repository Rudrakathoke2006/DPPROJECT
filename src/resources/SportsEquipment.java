package resources;

public class SportsEquipment implements Equipment {

    @Override
    public String getEquipmentName() {
        return "Sports Equipment Kit";
    }

    @Override
    public void displayDetails() {
        view.ConsoleView.println(
                "Equipment: Sports Equipment Kit"
        );
    }
}