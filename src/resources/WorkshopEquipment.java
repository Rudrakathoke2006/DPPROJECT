package resources;

public class WorkshopEquipment implements Equipment {

    @Override
    public String getEquipmentName() {
        return "Projector and Training Equipment";
    }

    @Override
    public void displayDetails() {
        view.ConsoleView.println(
                "Equipment: Projector and Training Equipment"
        );
    }
}