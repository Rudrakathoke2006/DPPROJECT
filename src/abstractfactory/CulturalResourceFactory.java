package abstractfactory;

import resources.CulturalEquipment;
import resources.CulturalSupportStaff;
import resources.CulturalVenue;

import resources.Equipment;
import resources.SupportStaff;
import resources.Venue;

public class CulturalResourceFactory
        implements ResourceFactory {

    @Override
    public Venue createVenue() {
        return new CulturalVenue();
    }

    @Override
    public Equipment createEquipment() {
        return new CulturalEquipment();
    }

    @Override
    public SupportStaff createSupportStaff() {
        return new CulturalSupportStaff();
    }
}