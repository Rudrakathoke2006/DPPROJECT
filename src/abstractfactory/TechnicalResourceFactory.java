package abstractfactory;

import resources.Equipment;
import resources.SupportStaff;
import resources.Venue;

import resources.TechnicalEquipment;
import resources.TechnicalSupportStaff;
import resources.TechnicalVenue;

public class TechnicalResourceFactory
        implements ResourceFactory {

    @Override
    public Venue createVenue() {
        return new TechnicalVenue();
    }

    @Override
    public Equipment createEquipment() {
        return new TechnicalEquipment();
    }

    @Override
    public SupportStaff createSupportStaff() {
        return new TechnicalSupportStaff();
    }
}