package resources;

public class TechnicalSupportStaff implements SupportStaff {

    @Override
    public String getStaffType() {
        return "Technical Support Team";
    }

    @Override
    public void displayDetails() {
        view.ConsoleView.println(
                "Support Staff: Technical Support Team"
        );
    }
}