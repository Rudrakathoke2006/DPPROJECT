package abstractfactory;

import resources.Equipment;
import resources.SupportStaff;
import resources.Venue;

import resources.SportsEquipment;
import resources.SportsSupportStaff;
import resources.SportsVenue;

public class SportsResourceFactory
        implements ResourceFactory {

    @Override
    public Venue createVenue() {
        return new SportsVenue();
    }

    @Override
    public Equipment createEquipment() {
        return new SportsEquipment();
    }

    @Override
    public SupportStaff createSupportStaff() {
        return new SportsSupportStaff();
    }
}