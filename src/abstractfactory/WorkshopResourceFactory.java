package abstractfactory;

import resources.Equipment;
import resources.SupportStaff;
import resources.Venue;

import resources.WorkshopEquipment;
import resources.WorkshopSupportStaff;
import resources.WorkshopVenue;

public class WorkshopResourceFactory
        implements ResourceFactory {

    @Override
    public Venue createVenue() {
        return new WorkshopVenue();
    }

    @Override
    public Equipment createEquipment() {
        return new WorkshopEquipment();
    }

    @Override
    public SupportStaff createSupportStaff() {
        return new WorkshopSupportStaff();
    }
}