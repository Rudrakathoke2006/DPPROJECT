import campus.web.ViewData;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
public class WebDataRegressionTest {
 public static void main(String[] args) throws Exception {
  Path file=Paths.get("data/events.csv"); Files.createDirectories(file.getParent());
  Files.writeString(file,"\uFEFF eventId ,eventName,eventType,eventLevel,organizer,date,venue,status\nBOM1,Name,TECHNICAL,DEPARTMENT,Faculty,22-12-2027,Lab,PENDING\n",StandardCharsets.UTF_8);
  if(!"BOM1".equals(ViewData.rows("events").get(0).get("eventId")))throw new AssertionError("BOM header mapping");
  Files.writeString(file,"eventId,eventName,eventType,eventLevel,organizer,date,venue,status\n,Missing ID,TECHNICAL,DEPARTMENT,Faculty,22-12-2027,Lab,PENDING\n",StandardCharsets.UTF_8);
  try { ViewData.rows("events"); throw new AssertionError("Missing ID accepted"); } catch(java.io.IOException expected) { }
  try { new filehandling.EventFileHandler().saveEvent(null); throw new AssertionError("Null event accepted"); } catch(IllegalArgumentException expected) { }
  System.out.println("Web CSV BOM, whitespace, missing ID and save validation PASSED");
 }
}
