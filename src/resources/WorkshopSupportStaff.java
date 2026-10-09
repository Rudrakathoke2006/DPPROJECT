package resources;

public class WorkshopSupportStaff implements SupportStaff {

    @Override
    public String getStaffType() {
        return "Training Support Team";
    }

    @Override
    public void displayDetails() {
        System.out.println(
                "Support Staff: Training Support Team"
        );
    }
}