package template;

public class SportsPreparation extends EventPreparationTemplate
{
    @Override
    protected boolean checkCategoryRequirements(String[] resources)
    {
        System.out.println("Checking sports venue and sports kit...");
        return !resources[3].trim().isEmpty() && !resources[4].trim().isEmpty();
    }
}
