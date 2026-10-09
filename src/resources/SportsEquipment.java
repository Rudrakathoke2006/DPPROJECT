package resources;

public class SportsEquipment implements Equipment {

    @Override
    public String getEquipmentName() {
        return "Sports Equipment Kit";
    }

    @Override
    public void displayDetails() {
        System.out.println(
                "Equipment: Sports Equipment Kit"
        );
    }
}