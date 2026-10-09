package memento;

import model.EventMemento;
import model.EventSpecification;
import model.User;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Single validated path for event metadata edits and undo. Does not touch registrations. */
public class EventEditService {
    private static final String EVENTS = "data/events.csv";
    private static final String DETAILS = "data/event_details.csv";
    private static final String APPROVALS = "data/approvals.csv";
    private final EventHistoryCaretaker caretaker = new EventHistoryCaretaker();

    public boolean edit(User user, Scanner in) {
        if (!authorized(user)) return denied();
        System.out.print("Event ID to edit: "); String id = in.nextLine().trim();
        EventMemento current = load(id); if (current == null) { System.out.println("Event not found."); return false; }
        show(current);
        System.out.println("Enter new values (press Enter to keep current value).");
        String name = ask(in, "Name", current.getEventName());
        String date = ask(in, "Date", current.getDate());
        String venue = ask(in, "Proposed venue", current.getVenue());
        String desc = ask(in, "Description", current.getDescription());
        String budget = ask(in, "Budget", current.getBudget());
        String capacity = ask(in, "Capacity", current.getCapacity());
        String duration = ask(in, "Duration hours", current.getDurationHours());
        String equipment = ask(in, "Equipment", current.getEquipment());
        String instructions = ask(in, "Special instructions", current.getSpecialInstructions());
        EventMemento next;
        try {
            new EventSpecification(desc, Double.parseDouble(orDefault(budget,"0")), Integer.parseInt(orDefault(capacity,"0")), Integer.parseInt(orDefault(duration,"0")), equipment, instructions);
            next = new EventMemento(id, clean(name), clean(date), clean(venue), clean(desc), orDefault(budget,"0"), orDefault(capacity,"0"), orDefault(duration,"0"), clean(equipment), clean(instructions));
        } catch (RuntimeException ex) { System.out.println("Invalid optional values. No changes saved: " + ex.getMessage()); return false; }
        if (same(current,next)) { System.out.println("No changes detected."); return true; }
        // Snapshot first; if snapshot persistence fails, do not mutate the event.
        if (!caretaker.record(current, user.getUsername(), "EDIT_BEFORE_CHANGE")) return false;
        if (!saveMetadata(next)) { System.out.println("Could not save changes; event was not updated."); return false; }
        boolean reapproval = resetApproval(id, name);
        System.out.println("Event updated successfully.");
        if (reapproval) System.out.println("Approval reset to PENDING and routed back to Faculty. Previous approvals remain documented in event history.");
        else System.out.println("Metadata updated. No approval record was found to reopen; please verify approvals.csv.");
        System.out.println("Before:"); show(current); System.out.println("After:"); show(next);
        return true;
    }

    public boolean undoLast(User user, Scanner in) {
        if (!authorized(user)) return denied();
        System.out.print("Event ID to undo: "); String id = in.nextLine().trim();
        List<EventMemento> versions = caretaker.undoableSnapshots(id);
        if (versions.isEmpty()) { System.out.println("No undo history for this event."); return false; }
        EventMemento target = versions.get(versions.size()-1);
        EventMemento current = load(id); if (current == null) { System.out.println("Event not found."); return false; }
        // Record current state as an audit snapshot before restoring; the restore itself is another history entry.
        if (!caretaker.record(current, user.getUsername(), "UNDO_BEFORE_RESTORE")) return false;
        if (!saveMetadata(target)) return false;
        boolean reapproval = resetApproval(id, target.getEventName());
        caretaker.record(target, user.getUsername(), "UNDO_RESTORED_VERSION");
        System.out.println("Undo complete. Restored editable metadata only; registrations and approval decisions were not restored.");
        System.out.println("Before undo:"); show(current); System.out.println("After undo:"); show(target);
        System.out.println("Reapproval required: " + (reapproval ? "YES, status is PENDING and Faculty routing reopened." : "CHECK APPROVALS: no approval row was reset."));
        return true;
    }

    public void showHistory(User user, Scanner in) {
        if (!authorized(user)) { denied(); return; }
        System.out.print("Event ID for history: "); caretaker.displayHistory(in.nextLine().trim());
    }

    private boolean authorized(User u) { return u != null && ("FACULTY".equalsIgnoreCase(u.getRole()) || "ADMIN".equalsIgnoreCase(u.getRole())); }
    private boolean denied() { System.out.println("Access denied: only Faculty/Admin can edit, undo, or view event history."); return false; }
    private String ask(Scanner in,String label,String old) { System.out.print(label+" ["+old+"]: "); String v=in.nextLine(); return v.trim().isEmpty()?old:v.trim(); }
    private String orDefault(String s,String d) { return s==null||s.trim().isEmpty()?d:s.trim(); }
    private String clean(String s) { return EventSpecification.sanitize(s); }
    private boolean same(EventMemento a,EventMemento b) { return a.getEventName().equals(b.getEventName())&&a.getDate().equals(b.getDate())&&a.getVenue().equals(b.getVenue())&&a.getDescription().equals(b.getDescription())&&a.getBudget().equals(b.getBudget())&&a.getCapacity().equals(b.getCapacity())&&a.getDurationHours().equals(b.getDurationHours())&&a.getEquipment().equals(b.getEquipment())&&a.getSpecialInstructions().equals(b.getSpecialInstructions()); }
    private void show(EventMemento m) { System.out.println("Event ID: "+m.getEventId()+" | Name: "+m.getEventName()+" | Date: "+m.getDate()+" | Venue: "+m.getVenue()+" | Description: "+m.getDescription()+" | Budget: "+m.getBudget()+" | Capacity: "+m.getCapacity()+" | Duration: "+m.getDurationHours()+" | Equipment: "+m.getEquipment()+" | Instructions: "+m.getSpecialInstructions()); }

    private EventMemento load(String id) {
        String[] e = find(EVENTS,id,8); if(e==null)return null;
        String[] d = find(DETAILS,id,7);
        return new EventMemento(id,e[1],e[5],e[6],d==null?EventSpecification.DEFAULT_DESCRIPTION:d[1],d==null?"0":d[2],d==null?"0":d[3],d==null?"0":d[4],d==null?"None":d[5],d==null?"None":d[6]);
    }
    private String[] find(String path,String id,int min) {
        File f=new File(path); if(!f.exists())return null;
        try(BufferedReader r=new BufferedReader(new FileReader(f))){r.readLine();String line;while((line=r.readLine())!=null){String[] d=line.split(",",-1);if(d.length>=min&&d[0].equalsIgnoreCase(id))return d;}}catch(IOException e){System.out.println("Read error: "+e.getMessage());} return null;
    }
    private boolean saveMetadata(EventMemento m) {
        String[] event=find(EVENTS,m.getEventId(),8); if(event==null)return false;
        event[1]=clean(m.getEventName()); event[5]=clean(m.getDate()); event[6]=clean(m.getVenue()); event[7]="PENDING";
        if(!replaceRow(EVENTS,m.getEventId(),join(event)))return false;
        String detail=clean(m.getEventId())+","+clean(m.getDescription())+","+orDefault(m.getBudget(),"0")+","+orDefault(m.getCapacity(),"0")+","+orDefault(m.getDurationHours(),"0")+","+clean(m.getEquipment())+","+clean(m.getSpecialInstructions());
        if(find(DETAILS,m.getEventId(),7)==null){ ensureHeader(DETAILS,"eventId,description,budget,capacity,durationHours,equipment,specialInstructions"); try{Files.write(Paths.get(DETAILS),Collections.singletonList(detail),StandardCharsets.UTF_8,StandardOpenOption.APPEND);}catch(IOException ex){return false;} }
        else if(!replaceRow(DETAILS,m.getEventId(),detail))return false;
        return true;
    }
    private boolean resetApproval(String id,String name) {
        String[] a=find(APPROVALS,id,8); if(a==null)return false;
        a[1]=clean(name); a[4]=new filehandling.UserFileHandler().findUsernameByRole("FACULTY"); if(a[4]==null||a[4].trim().isEmpty())return false;
        a[5]="FACULTY"; a[6]="PENDING"; a[7]="Reapproval required after event metadata change.";
        return replaceRow(APPROVALS,id,join(a));
    }
    private void ensureHeader(String path,String header){File f=new File(path);if(f.exists())return;try{File p=f.getParentFile();if(p!=null)p.mkdirs();Files.write(f.toPath(),Collections.singletonList(header),StandardCharsets.UTF_8);}catch(IOException e){throw new IllegalStateException(e);}}
    private boolean replaceRow(String path,String id,String replacement) {
        Path original=Paths.get(path), temp=Paths.get(path+".tmp"); boolean found=false;
        try {ensureHeader(path,path.equals(EVENTS)?"eventId,eventName,eventType,eventLevel,organizer,date,venue,status":path.equals(DETAILS)?"eventId,description,budget,capacity,durationHours,equipment,specialInstructions":"eventId,eventName,eventType,eventLevel,approverUsername,approverRole,status,remarks");
            try(BufferedReader r=Files.newBufferedReader(original,StandardCharsets.UTF_8);BufferedWriter w=Files.newBufferedWriter(temp,StandardCharsets.UTF_8)){String header=r.readLine();w.write(header==null?"":header);w.newLine();String line;while((line=r.readLine())!=null){String[] d=line.split(",",-1);if(d.length>0&&d[0].equalsIgnoreCase(id)){w.write(replacement);found=true;}else w.write(line);w.newLine();}}
            if(!found){Files.deleteIfExists(temp);return false;} Files.move(temp,original,StandardCopyOption.REPLACE_EXISTING);return true;
        }catch(IOException e){try{Files.deleteIfExists(temp);}catch(IOException ignored){}System.out.println("Write error: "+e.getMessage());return false;}
    }
    private String join(String[] d){StringJoiner j=new StringJoiner(",");for(String s:d)j.add(clean(s));return j.toString();}
}
