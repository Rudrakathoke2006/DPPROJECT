package template;

public abstract class EventPreparationTemplate
{
    // Template Method: the common preparation sequence cannot be overridden.
    public final void prepareEvent(String[] event, String[] resources)
    {
        System.out.println("\n===== EVENT READINESS CHECK =====");

        if (event == null)
        {
            System.out.println("NOT READY: Event not found.");
            return;
        }

        if (event.length < 8 || event[0].trim().isEmpty()
                || event[1].trim().isEmpty() || event[2].trim().isEmpty())
        {
            System.out.println("NOT READY: Invalid event record.");
            return;
        }

        System.out.println("Event: " + event[1]);
        System.out.println("Check 1: Event record valid - PASS");

        if (!event[7].trim().equalsIgnoreCase("APPROVED"))
        {
            System.out.println("NOT READY: Event is not approved.");
            return;
        }
        System.out.println("Check 2: Event approved - PASS");

        if (resources == null || resources.length < 6
                || resources[3].trim().isEmpty()
                || resources[4].trim().isEmpty()
                || resources[5].trim().isEmpty())
        {
            System.out.println("NOT READY: Resources not allocated or incomplete.");
            return;
        }
        System.out.println("Check 3: Resources allocated - PASS");

        if (!resources[2].trim().equalsIgnoreCase(event[2].trim()))
        {
            System.out.println("NOT READY: Resource category does not match event type.");
            return;
        }

        boolean categoryReady = checkCategoryRequirements(resources);
        if (categoryReady)
        {
            System.out.println("Check 4: Category requirements - PASS");
            System.out.println("FINAL RESULT: READY");
        }
        else
        {
            System.out.println("Check 4: Category requirements - FAIL");
            System.out.println("FINAL RESULT: NOT READY");
        }
    }

    // Subclasses customize this step for their event category.
    protected abstract boolean checkCategoryRequirements(String[] resources);
}
