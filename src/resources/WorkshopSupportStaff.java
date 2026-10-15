package resources;

public class WorkshopSupportStaff implements SupportStaff {

    @Override
    public String getStaffType() {
        return "Training Support Team";
    }

    @Override
    public void displayDetails() {
        view.ConsoleView.println(
                "Support Staff: Training Support Team"
        );
    }
}