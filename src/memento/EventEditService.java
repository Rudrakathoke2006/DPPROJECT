package memento;

import model.EventMemento;
import model.EventSpecification;
import model.User;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import filehandling.CsvTransaction;

/** Single validated path for event metadata edits and undo. Does not touch registrations. */
public class EventEditService {
    private static final String EVENTS = "data/events.csv";
    private static final String DETAILS = "data/event_details.csv";
    private static final String APPROVALS = "data/approvals.csv";
    private final EventHistoryCaretaker caretaker = new EventHistoryCaretaker();

    public boolean edit(User user, Scanner in) {
        if (!authorized(user)) return denied();
        view.ConsoleView.print("Event ID to edit: "); String id = view.ConsoleView.readLine(in).trim();
        EventMemento current = load(id); if (current == null) { view.ConsoleView.println("Event not found."); return false; }
        if (new filehandling.ResourceFileHandler().resourceExists(id)) {
            view.ConsoleView.println("Cannot edit an event with allocated resources. Contact administrator to release allocation first."); return false;
        }
        show(current);
        view.ConsoleView.println("Enter new values (press Enter to keep current value).");
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
        } catch (RuntimeException ex) { view.ConsoleView.println("Invalid optional values. No changes saved: " + ex.getMessage()); return false; }
        if (clean(name).isEmpty() || clean(date).isEmpty() || clean(venue).isEmpty()) { view.ConsoleView.println("Name, date and venue are required. No changes saved."); return false; }
        if (same(current,next)) { view.ConsoleView.println("No changes detected."); return true; }
        if (!approvalCanReopen(id)) { view.ConsoleView.println("Approval route missing; edit cancelled to prevent inconsistent status."); return false; }
        // Snapshot first; if snapshot persistence fails, do not mutate the event.
        try (CsvTransaction tx = new CsvTransaction(EVENTS, DETAILS, APPROVALS, "data/event_history.csv")) {
            if (!caretaker.record(current, user.getUsername(), "EDIT_BEFORE_CHANGE")) return false;
            if (!saveMetadata(next) || !resetApproval(id, name)) {
                view.ConsoleView.println("Could not save all event/approval changes; rolling back."); return false;
            }
            tx.commit();
        } catch (IOException ex) { view.ConsoleView.println("Edit failed; rollback attempted: " + ex.getMessage()); return false; }
        new mediator.UniversityEventCoordinator().eventEdited(id, "EDIT by " + user.getUsername());   // Mediator signal
        boolean reapproval = true;
        view.ConsoleView.println("Event updated successfully.");
        if (reapproval) view.ConsoleView.println("Approval reset to PENDING and routed back to Faculty. Previous approvals remain documented in event history.");
        else view.ConsoleView.println("Metadata updated. No approval record was found to reopen; please verify approvals.csv.");
        view.ConsoleView.println("Before:"); show(current); view.ConsoleView.println("After:"); show(next);
        return true;
    }

    public boolean undoLast(User user, Scanner in) {
        if (!authorized(user)) return denied();
        view.ConsoleView.print("Event ID to undo: "); String id = view.ConsoleView.readLine(in).trim();
        List<EventMemento> versions = caretaker.undoableSnapshots(id);
        if (versions.isEmpty()) { view.ConsoleView.println("No undo history for this event."); return false; }
        EventMemento target = versions.get(versions.size()-1);
        EventMemento current = load(id); if (current == null) { view.ConsoleView.println("Event not found."); return false; }
        if (new filehandling.ResourceFileHandler().resourceExists(id)) {
            view.ConsoleView.println("Cannot undo while resources are allocated; release allocation first."); return false;
        }
        if (!approvalCanReopen(id)) { view.ConsoleView.println("Approval route missing; undo cancelled."); return false; }
        // Record current state as an audit snapshot before restoring; the restore itself is another history entry.
        try (CsvTransaction tx = new CsvTransaction(EVENTS, DETAILS, APPROVALS, "data/event_history.csv")) {
            if (!caretaker.record(current, user.getUsername(), "UNDO_BEFORE_RESTORE")) return false;
            if (!saveMetadata(target) || !resetApproval(id, target.getEventName())) return false;
            if (!caretaker.record(target, user.getUsername(), "UNDO_RESTORED_VERSION")) return false;
            tx.commit();
        } catch (IOException ex) { view.ConsoleView.println("Undo failed; rollback attempted: " + ex.getMessage()); return false; }
        new mediator.UniversityEventCoordinator().eventEdited(id, "UNDO by " + user.getUsername());   // Mediator signal
        boolean reapproval = true;
        view.ConsoleView.println("Undo complete. Restored editable metadata only; registrations and approval decisions were not restored.");
        view.ConsoleView.println("Before undo:"); show(current); view.ConsoleView.println("After undo:"); show(target);
        view.ConsoleView.println("Reapproval required: " + (reapproval ? "YES, status is PENDING and Faculty routing reopened." : "CHECK APPROVALS: no approval row was reset."));
        return true;
    }

    public void showHistory(User user, Scanner in) {
        if (!authorized(user)) { denied(); return; }
        view.ConsoleView.print("Event ID for history: "); caretaker.displayHistory(view.ConsoleView.readLine(in).trim());
    }

    private boolean authorized(User u) { return u != null && ("FACULTY".equalsIgnoreCase(u.getRole()) || "ADMIN".equalsIgnoreCase(u.getRole())); }
    private boolean denied() { view.ConsoleView.println("Access denied: only Faculty/Admin can edit, undo, or view event history."); return false; }
    private String ask(Scanner in,String label,String old) { view.ConsoleView.print(label+" ["+old+"]: "); String v=view.ConsoleView.readLine(in); return v.trim().isEmpty()?old:v.trim(); }
    private String orDefault(String s,String d) { return s==null||s.trim().isEmpty()?d:s.trim(); }
    private String clean(String s) { return EventSpecification.sanitize(s); }
    private boolean same(EventMemento a,EventMemento b) { return a.getEventName().equals(b.getEventName())&&a.getDate().equals(b.getDate())&&a.getVenue().equals(b.getVenue())&&a.getDescription().equals(b.getDescription())&&a.getBudget().equals(b.getBudget())&&a.getCapacity().equals(b.getCapacity())&&a.getDurationHours().equals(b.getDurationHours())&&a.getEquipment().equals(b.getEquipment())&&a.getSpecialInstructions().equals(b.getSpecialInstructions()); }
    private void show(EventMemento m) { view.ConsoleView.println("Event ID: "+m.getEventId()+" | Name: "+m.getEventName()+" | Date: "+m.getDate()+" | Venue: "+m.getVenue()+" | Description: "+m.getDescription()+" | Budget: "+m.getBudget()+" | Capacity: "+m.getCapacity()+" | Duration: "+m.getDurationHours()+" | Equipment: "+m.getEquipment()+" | Instructions: "+m.getSpecialInstructions()); }

    private EventMemento load(String id) {
        String[] e = find(EVENTS,id,8); if(e==null)return null;
        String[] d = find(DETAILS,id,7);
        return new EventMemento(id,e[1],e[5],e[6],d==null?EventSpecification.DEFAULT_DESCRIPTION:d[1],d==null?"0":d[2],d==null?"0":d[3],d==null?"0":d[4],d==null?"None":d[5],d==null?"None":d[6]);
    }
    private String[] find(String path,String id,int min) {
        File f=new File(path); if(!f.exists())return null;
        try(BufferedReader r=new BufferedReader(new FileReader(f))){r.readLine();String line;while((line=r.readLine())!=null){String[] d=line.split(",",-1);if(d.length>=min&&d[0].equalsIgnoreCase(id))return d;}}catch(IOException e){view.ConsoleView.println("Read error: "+e.getMessage());} return null;
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
    private boolean approvalCanReopen(String id) {
        String[] approval=find(APPROVALS,id,8);
        String faculty=new filehandling.UserFileHandler().findUsernameByRole("FACULTY");
        return approval!=null && faculty!=null && !faculty.trim().isEmpty();
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
        }catch(IOException e){try{Files.deleteIfExists(temp);}catch(IOException ignored){}view.ConsoleView.println("Write error: "+e.getMessage());return false;}
    }
    private String join(String[] d){StringJoiner j=new StringJoiner(",");for(String s:d)j.add(clean(s));return j.toString();}
}
