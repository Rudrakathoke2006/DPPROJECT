package resources;

public class TechnicalSupportStaff implements SupportStaff {

    @Override
    public String getStaffType() {
        return "Technical Support Team";
    }

    @Override
    public void displayDetails() {
        System.out.println(
                "Support Staff: Technical Support Team"
        );
    }
}