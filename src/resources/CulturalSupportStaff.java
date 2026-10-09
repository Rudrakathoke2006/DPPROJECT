package resources;

public class CulturalSupportStaff implements SupportStaff {

    @Override
    public String getStaffType() {
        return "Stage Management Team";
    }

    @Override
    public void displayDetails() {
        System.out.println(
                "Support Staff: Stage Management Team"
        );
    }
}