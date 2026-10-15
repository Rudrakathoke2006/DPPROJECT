package template;

import filehandling.EventFileHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class EventPreparationService
{
    public void runConsoleFlow(Scanner scanner)
    {
        EventFileHandler eventHandler = new EventFileHandler();

        System.out.print("Enter Event ID: ");
        String eventId = scanner.nextLine().trim();
        String[] event = eventHandler.findEventById(eventId);

        if (event == null)
        {
            System.out.println("NOT READY: Event not found.");
            return;
        }

        String[] resources = findResources(eventId);
        EventPreparationTemplate preparation;

        switch (event[2].trim().toUpperCase())
        {
            case "TECHNICAL":
                preparation = new TechnicalPreparation();
                break;
            case "CULTURAL":
                preparation = new CulturalPreparation();
                break;
            case "SPORTS":
                preparation = new SportsPreparation();
                break;
            case "WORKSHOP":
                preparation = new WorkshopPreparation();
                break;
            default:
                System.out.println("NOT READY: Unsupported event type.");
                return;
        }

        preparation.prepareEvent(event, resources);
    }

    private String[] findResources(String eventId)
    {
        try (BufferedReader reader = new BufferedReader(
                new FileReader("data/resources.csv")))
        {
            reader.readLine(); // Skip CSV header.
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] data = line.split(",", -1);
                if (data.length >= 6 && data[0].trim().equalsIgnoreCase(eventId))
                {
                    return data;
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Could not read resource records: " + e.getMessage());
        }
        return null;
    }
}
