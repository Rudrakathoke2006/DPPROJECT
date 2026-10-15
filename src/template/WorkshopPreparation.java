package template;

public class WorkshopPreparation extends EventPreparationTemplate
{
    @Override
    protected boolean checkCategoryRequirements(String[] resources)
    {
        System.out.println("Checking training room and learning setup...");
        return !resources[3].trim().isEmpty() && !resources[4].trim().isEmpty();
    }
}
