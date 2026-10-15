package resources;

public class SportsSupportStaff implements SupportStaff {

    @Override
    public String getStaffType() {
        return "Sports Management Team";
    }

    @Override
    public void displayDetails() {
        view.ConsoleView.println(
                "Support Staff: Sports Management Team"
        );
    }
}