package mediator;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Append-only data/coordination_log.csv: eventId,eventType,signal,outcome,detail,timestamp. */
final class CoordinationLog {
    static final String FILE = "data/coordination_log.csv";
    private static final String HEADER = "eventId,eventType,signal,outcome,detail,timestamp";
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Always appends. */
    void append(String eventId, String type, String signal, String outcome, String detail) {
        write(eventId, type, signal, outcome, detail);
    }

    /** Idempotency guard: skips the row if the event's latest row has the same signal/outcome/detail. */
    boolean appendIfChanged(String eventId, String type, String signal, String outcome, String detail) {
        String[] last = latest(eventId);
        if (last != null && last[2].equals(signal) && last[3].equals(outcome) && last[4].equals(clean(detail))) return false;
        write(eventId, type, signal, outcome, detail);
        return true;
    }

    String[] latest(String eventId) {
        List<String[]> rows = CsvRows.read(FILE, 6);
        for (int i = rows.size() - 1; i >= 0; i--) if (rows.get(i)[0].equalsIgnoreCase(eventId)) return rows.get(i);
        return null;
    }

    private void write(String eventId, String type, String signal, String outcome, String detail) {
        File f = new File(FILE);
        try {
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            boolean fresh = !f.exists();
            try (PrintWriter w = new PrintWriter(new FileWriter(f, true))) {
                if (fresh) w.println(HEADER);
                w.println(clean(eventId) + "," + clean(type) + "," + clean(signal) + "," + clean(outcome) + ","
                        + clean(detail) + "," + LocalDateTime.now().format(TS));
            }
        } catch (IOException e) {
            // Coordination logging must never break the business workflow.
            view.ConsoleView.println("Warning: coordination log not written: " + e.getMessage());
        }
    }

    private static String clean(String s) {
        return s == null ? "" : s.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }
}
