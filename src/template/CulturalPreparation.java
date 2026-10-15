package template;

public class CulturalPreparation extends EventPreparationTemplate
{
    @Override
    protected boolean checkCategoryRequirements(String[] resources)
    {
        System.out.println("Checking stage and audio facilities...");
        return !resources[3].trim().isEmpty() && !resources[4].trim().isEmpty();
    }
}
