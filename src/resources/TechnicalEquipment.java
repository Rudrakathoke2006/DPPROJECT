package resources;

public class TechnicalEquipment implements Equipment {

    @Override
    public String getEquipmentName() {
        return "Computers and Projector";
    }

    @Override
    public void displayDetails() {
        System.out.println(
                "Equipment: Computers and Projector"
        );
    }
}