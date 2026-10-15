package memento;

import model.EventMemento;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/** Persistent caretaker. Values are Base64 encoded so commas/newlines cannot corrupt the CSV. */
public class EventHistoryCaretaker {
    private static final String FILE = "data/event_history.csv";
    private static final String HEADER = "eventId,version,timestamp,actor,action,eventName,date,venue,description,budget,capacity,durationHours,equipment,specialInstructions";

    public synchronized boolean record(EventMemento m, String actor, String action) {
        try {
            ensureFile();
            int version = history(m.getEventId()).size() + 1;
            String row = enc(m.getEventId()) + "," + version + "," + enc(LocalDateTime.now().toString()) + "," + enc(actor) + "," + enc(action)
                    + "," + enc(m.getEventName()) + "," + enc(m.getDate()) + "," + enc(m.getVenue()) + "," + enc(m.getDescription())
                    + "," + enc(m.getBudget()) + "," + enc(m.getCapacity()) + "," + enc(m.getDurationHours()) + "," + enc(m.getEquipment()) + "," + enc(m.getSpecialInstructions());
            Files.write(Paths.get(FILE), Collections.singletonList(row), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) { view.ConsoleView.println("Could not save event history: " + e.getMessage()); return false; }
    }

    public synchronized List<EventMemento> history(String eventId) {
        List<EventMemento> result = new ArrayList<>();
        File f = new File(FILE); if (!f.exists()) return result;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            r.readLine(); String line;
            while ((line = r.readLine()) != null) {
                String[] d = line.split(",", -1);
                if (d.length < 14) continue;
                try { if (!dec(d[0]).equalsIgnoreCase(eventId)) continue;
                    result.add(new EventMemento(dec(d[0]), dec(d[5]), dec(d[6]), dec(d[7]), dec(d[8]), dec(d[9]), dec(d[10]), dec(d[11]), dec(d[12]), dec(d[13])));
                } catch (IllegalArgumentException ignored) { /* ignore malformed legacy rows */ }
            }
        } catch (IOException e) { view.ConsoleView.println("Could not read event history: " + e.getMessage()); }
        return result;
    }

    /** Snapshots captured immediately before successful edits, in chronological order. */
    /** Each successful undo consumes one edit snapshot; repeated undo walks backwards. */
    public synchronized List<EventMemento> undoableSnapshots(String eventId) {
        List<EventMemento> edits = new ArrayList<>();
        File f = new File(FILE); if (!f.exists()) return edits;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            r.readLine(); String line;
            while ((line = r.readLine()) != null) {
                String[] d = line.split(",", -1); if (d.length < 14) continue;
                try {
                    if (!dec(d[0]).equalsIgnoreCase(eventId)) continue;
                    String action = dec(d[4]);
                    if (action.equals("EDIT_BEFORE_CHANGE")) edits.add(new EventMemento(dec(d[0]),dec(d[5]),dec(d[6]),dec(d[7]),dec(d[8]),dec(d[9]),dec(d[10]),dec(d[11]),dec(d[12]),dec(d[13])));
                    else if (action.equals("UNDO_RESTORED_VERSION") && !edits.isEmpty()) edits.remove(edits.size()-1);
                } catch (IllegalArgumentException ignored) { }
            }
        } catch (IOException e) { view.ConsoleView.println("Could not read event history: "+e.getMessage()); }
        return edits;
    }

    public synchronized void displayHistory(String eventId) {
        File f = new File(FILE); if (!f.exists()) { view.ConsoleView.println("No edit history yet."); return; }
        boolean found = false;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            r.readLine(); String line;
            while ((line = r.readLine()) != null) {
                String[] d = line.split(",", -1); if (d.length < 14) continue;
                if (dec(d[0]).equalsIgnoreCase(eventId)) {
                    found = true;
                    view.ConsoleView.println("Version " + d[1] + " | " + dec(d[2]) + " | " + dec(d[3]) + " | " + dec(d[4]));
                    view.ConsoleView.println("  Name: " + dec(d[5]) + " | Date: " + dec(d[6]) + " | Venue: " + dec(d[7]) + " | Description: " + dec(d[8]));
                }
            }
        } catch (IOException e) { view.ConsoleView.println("Could not display history: " + e.getMessage()); }
        if (!found) view.ConsoleView.println("No edit history for this event.");
    }

    private void ensureFile() throws IOException {
        File f = new File(FILE); File p = f.getParentFile(); if (p != null) p.mkdirs();
        if (!f.exists()) Files.write(f.toPath(), Collections.singletonList(HEADER), StandardCharsets.UTF_8);
    }
    private static String enc(String s) { return Base64.getEncoder().encodeToString((s == null ? "" : s).getBytes(StandardCharsets.UTF_8)); }
    private static String dec(String s) { return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8); }
}
