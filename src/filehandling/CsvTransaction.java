package filehandling;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** Best-effort rollback of a coordinated set of CSV changes on a single process.
 * Keep calls serialized; this is not a crash-safe database transaction. */
public final class CsvTransaction implements AutoCloseable {
    private final Map<Path,byte[]> originals = new LinkedHashMap<>();
    private final Set<Path> missing = new HashSet<>();
    private boolean committed;
    public CsvTransaction(String... paths) throws IOException {
        for (String s : paths) {
            Path p=Paths.get(s);
            if (Files.exists(p)) originals.put(p,Files.readAllBytes(p));
            else missing.add(p);
        }
    }
    public void commit(){committed=true;}
    @Override public void close() throws IOException {
        if(committed)return;
        IOException failure=null;
        for(Map.Entry<Path,byte[]> entry:originals.entrySet())try{Files.write(entry.getKey(),entry.getValue());}catch(IOException e){failure=e;}
        for(Path p:missing)try{Files.deleteIfExists(p);}catch(IOException e){failure=e;}
        if(failure!=null)throw failure;
    }
}
