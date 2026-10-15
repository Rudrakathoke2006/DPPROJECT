package resources;

public class CulturalSupportStaff implements SupportStaff {

    @Override
    public String getStaffType() {
        return "Stage Management Team";
    }

    @Override
    public void displayDetails() {
        view.ConsoleView.println(
                "Support Staff: Stage Management Team"
        );
    }
}