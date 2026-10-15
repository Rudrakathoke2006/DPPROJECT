package view;

public final class RoleMenuView {
    private RoleMenuView() {}
    public static void showMenu(String role) {
        System.out.println("\n---------------- " + role + " MENU ----------------");
        if (role.equals("STUDENT")) {
            System.out.println("1. View approved events");
            System.out.println("2. Register for event");
            System.out.println("3. View my registrations");
            System.out.println("4. View notification history");
            System.out.println("5. View Event Coordination Status (Mediator)");
            System.out.println("0. Logout");
        } else if (role.equals("FACULTY")) {
            System.out.println("1. View all events");
            System.out.println("2. Create event");
            System.out.println("3. Process pending approvals");
            System.out.println("4. Allocate resources to approved event");
            System.out.println("5. Publish event update");
            System.out.println("6. Create advanced event (Builder)");
            System.out.println("7. Duplicate existing event (Prototype)");
            System.out.println("8. Edit event (Memento)");
            System.out.println("9. Undo last event edit (Memento)");
            System.out.println("10. View event edit history");
            System.out.println("11. Smart Venue Selection (Strategy)");
            System.out.println("12. Run Event Preparation (Template Method)");
            System.out.println("13. View Event Coordination Status (Mediator)");
            System.out.println("0. Logout");
        } else if (role.equals("HOD") || role.equals("DEAN")) {
            System.out.println("1. View all events");
            System.out.println("2. Process pending approvals");
            System.out.println("3. Publish event update");
            System.out.println("4. View Event Coordination Status (Mediator)");
            System.out.println("0. Logout");
        } else if (role.equals("ADMIN")) {
            System.out.println("1. View all events");
            System.out.println("2. Create event");
            System.out.println("3. Allocate resources to approved event");
            System.out.println("4. Publish event update");
            System.out.println("5. View Event Coordination Status (Mediator)");
            System.out.println("0. Logout");
        }
    }

}
