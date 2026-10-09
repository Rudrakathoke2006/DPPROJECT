package abstractfactory;

import resources.Equipment;
import resources.SupportStaff;
import resources.Venue;

public interface ResourceFactory {

    Venue createVenue();

    Equipment createEquipment();

    SupportStaff createSupportStaff();
}