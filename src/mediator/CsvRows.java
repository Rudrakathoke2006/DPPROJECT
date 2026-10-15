package mediator;

import java.io.*;
import java.util.*;

/** Tiny read-only CSV helper for the module adapters (header skipped, short rows ignored). */
final class CsvRows {
    private CsvRows() { }
    static List<String[]> read(String path, int minColumns) {
        List<String[]> rows = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) return rows;
        try (BufferedReader r = new BufferedReader(new FileReader(f))) {
            r.readLine();
            String line;
            while ((line = r.readLine()) != null) {
                String[] d = line.split(",", -1);
                if (d.length >= minColumns) rows.add(d);
            }
        } catch (IOException e) {
            view.ConsoleView.println("Coordinator could not read " + path + ": " + e.getMessage());
        }
        return rows;
    }
}
