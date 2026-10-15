package strategy;
import java.util.*;
public final class VenueOption {
 public final String id,name; public final int capacity; public final double cost; public final Set<String> equipment; public final Set<String> unavailableDates;
 public VenueOption(String id,String name,int capacity,double cost,String equipment,String unavailable){this.id=id;this.name=name;this.capacity=capacity;this.cost=cost;this.equipment=items(equipment);this.unavailableDates=items(unavailable);}
 public static Set<String> items(String raw){Set<String> out=new LinkedHashSet<>();if(raw!=null)for(String v:raw.split(";"))if(!v.trim().isEmpty()&&!v.trim().equalsIgnoreCase("None"))out.add(v.trim().toLowerCase(Locale.ROOT));return out;}
 public int extraEquipment(Set<String> required){int count=0;for(String e:equipment)if(!required.contains(e))count++;return count;}
 public String toString(){return id+" | "+name+" | seats="+capacity+" | cost="+cost+" | equipment="+equipment;}
}
