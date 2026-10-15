package strategy;
import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.io.*;import java.util.*;
public class VenueInventoryRepository {
 private final Path path;
 public VenueInventoryRepository(){this(Paths.get("data/venues.csv"));}
 public VenueInventoryRepository(Path path){this.path=path;}
 public List<VenueOption> load() throws IOException {if(!Files.exists(path))throw new IOException("Missing venue inventory: "+path);List<VenueOption> out=new ArrayList<>();try(BufferedReader reader=Files.newBufferedReader(path,StandardCharsets.UTF_8)){reader.readLine();String line;while((line=reader.readLine())!=null){if(line.trim().isEmpty())continue;String[] a=line.split(",",-1);if(a.length!=6)throw new IOException("Invalid venue inventory row: "+line);try{out.add(new VenueOption(a[0],a[1],Integer.parseInt(a[2]),Double.parseDouble(a[3]),a[4],a[5]));}catch(NumberFormatException e){throw new IOException("Invalid venue capacity/cost: "+line,e);}}}return out;}
}
