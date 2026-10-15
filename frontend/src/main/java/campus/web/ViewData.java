package campus.web;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Read-only presentation model; every mutation is delegated to the original Java services. */
public final class ViewData {
    public static List<Map<String,String>> rows(String file) throws java.io.IOException {
        Path path = Paths.get("data", file + ".csv");
        if (!Files.exists(path)) return new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Map<String,String>> result = new ArrayList<>();
        if (lines.isEmpty()) return result;
        String[] headers = lines.get(0).split(",", -1);
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;
            String[] values = lines.get(i).split(",", -1);
            Map<String,String> row = new LinkedHashMap<>();
            for (int j=0; j<headers.length; j++) row.put(headers[j], j<values.length ? values[j] : "");
            result.add(row);
        }
        return result;
    }
    public static String h(Object value) {
        return value == null ? "" : value.toString().replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");
    }
    public static String label(String text) {
        if (text == null || text.isBlank()) return "";
        StringJoiner result = new StringJoiner(" ");
        for (String word:text.toLowerCase(Locale.ROOT).split("[_ ]+")) result.add(Character.toUpperCase(word.charAt(0))+word.substring(1));
        return result.toString();
    }
    public static LocalDate date(String text) {
        try { return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd-MM-uuuu")); }
        catch (Exception e) { return LocalDate.MAX; }
    }
    public static String prettyDate(String text) {
        LocalDate value = date(text);
        return value.equals(LocalDate.MAX) ? text : value.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH));
    }
    public static String isoDate(String text) { LocalDate value=date(text); return value.equals(LocalDate.MAX)?"":value.toString(); }
    public static Map<String,String> find(List<Map<String,String>> rows, String key, String value) {
        return rows.stream().filter(r -> value.equalsIgnoreCase(r.getOrDefault(key,""))).findFirst().orElse(new LinkedHashMap<>());
    }
}
