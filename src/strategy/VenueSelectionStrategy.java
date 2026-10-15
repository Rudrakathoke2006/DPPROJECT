package strategy;
import java.util.*;
public interface VenueSelectionStrategy {String name();Comparator<VenueOption> ranking(VenueRequirements requirements);String reason(VenueOption venue,VenueRequirements requirements);}
