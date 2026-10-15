package template;

public class TechnicalPreparation extends EventPreparationTemplate
{
    @Override
    protected boolean checkCategoryRequirements(String[] resources)
    {
        System.out.println("Checking lab and technical equipment...");
        return !resources[4].trim().isEmpty();
    }
}
