package resources;

public class CulturalEquipment implements Equipment {

    @Override
    public String getEquipmentName() {
        return "Sound System and Stage Equipment";
    }

    @Override
    public void displayDetails() {
        view.ConsoleView.println(
                "Equipment: Sound System and Stage Equipment"
        );
    }
}