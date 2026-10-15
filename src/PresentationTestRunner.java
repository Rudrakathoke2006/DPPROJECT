import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

/**
 * One-command automated test showcase. Each suite runs in its own temporary data workspace,
 * so regression tests cannot alter the application's sample CSV files.
 */
public final class PresentationTestRunner {
    private static final List<String> SUITES = Arrays.asList(
            "Member1BuilderPrototypeTest",
            "FullRegressionTest",
            "MediatorRegressionTest",
            "StrategyIntegrationTest",
            "StrategyConsoleRegressionTest"
    );
    private static final String[][] RESET_FILES = {
            {"events.csv", "eventId,eventName,eventType,eventLevel,organizer,date,venue,status"},
            {"approvals.csv", "eventId,eventName,eventType,eventLevel,approverUsername,approverRole,status,remarks"},
            {"event_details.csv", "eventId,description,budget,capacity,durationHours,equipment,specialInstructions"},
            {"event_history.csv", "eventId,version,timestamp,actor,action,eventName,date,venue,description,budget,capacity,durationHours,equipment,specialInstructions"},
            {"coordination_log.csv", "eventId,eventType,signal,outcome,detail,timestamp"},
            {"notifications.csv", "eventId,eventName,recipient,channel,message"},
            {"registrations.csv", "registrationId,eventId,eventName,studentUsername,notificationChannel"},
            {"resources.csv", "eventId,eventName,eventType,venue,equipment,supportStaff"}
    };

    private PresentationTestRunner() { }

    public static void main(String[] args) throws Exception {
        Path project = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path sourceData = project.resolve("data");
        Path classes = project.resolve("build").toAbsolutePath();
        if (!Files.isDirectory(sourceData) || !Files.isDirectory(classes)) {
            System.err.println("Run this from the project root after compiling with: javac -d build src/**/*.java src/*.java");
            System.exit(2);
        }

        int passed = 0;
        int failed = 0;
        System.out.println("============================================================");
        System.out.println(" UNIVERSITY EVENT SYSTEM - PRESENTATION TEST RUNNER");
        System.out.println(" Each suite uses temporary CSV data; project data is protected.");
        System.out.println("============================================================");

        for (int i = 0; i < SUITES.size(); i++) {
            String suite = SUITES.get(i);
            Path sandbox = Files.createTempDirectory("event-system-test-");
            try {
                Path sandboxData = sandbox.resolve("data");
                Files.createDirectories(sandboxData);
                try (java.util.stream.Stream<Path> files = Files.list(sourceData)) {
                    files.filter(Files::isRegularFile).forEach(path -> {
                        try { Files.copy(path, sandboxData.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING); }
                        catch (IOException e) { throw new RuntimeException(e); }
                    });
                }
                for (String[] file : RESET_FILES) {
                    Files.write(sandboxData.resolve(file[0]), Arrays.asList(file[1]), StandardCharsets.UTF_8);
                }

                System.out.println("\n[" + (i + 1) + "/" + SUITES.size() + "] " + suite);
                System.out.println("Purpose: " + purpose(suite));
                String java = Paths.get(System.getProperty("java.home"), "bin", isWindows() ? "java.exe" : "java").toString();
                Process process = new ProcessBuilder(java, "-cp", classes.toString(), suite)
                        .directory(sandbox.toFile()).inheritIO().start();
                int result = process.waitFor();
                if (result == 0) {
                    passed++;
                    System.out.println("[PASS] " + suite);
                } else {
                    failed++;
                    System.out.println("[FAIL] " + suite + " (exit code " + result + ")");
                }
            } finally {
                deleteTree(sandbox);
            }
        }
        System.out.println("\n============================================================");
        System.out.println("RESULT: " + passed + " passed, " + failed + " failed, " + SUITES.size() + " suites");
        System.out.println("Application CSV files were not used as test workspaces.");
        System.out.println("============================================================");
        if (failed > 0) System.exit(1);
    }

    private static String purpose(String suite) {
        if (suite.equals("Member1BuilderPrototypeTest")) return "Builder validation, event creation, and Prototype cloning";
        if (suite.equals("FullRegressionTest")) return "Event creation, editing/undo history, and venue strategy regression";
        if (suite.equals("MediatorRegressionTest")) return "Approval coordination, allocation, registration, notifications, and status";
        if (suite.equals("StrategyIntegrationTest")) return "Capacity-, budget-, and equipment-first venue selection";
        return "Console venue selection and allocation safeguards";
    }

    private static boolean isWindows() { return System.getProperty("os.name").toLowerCase().contains("win"); }

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) return;
        try (java.util.stream.Stream<Path> paths = Files.walk(root)) {
            Path[] all = paths.sorted((a, b) -> b.getNameCount() - a.getNameCount()).toArray(Path[]::new);
            for (Path path : all) Files.deleteIfExists(path);
        }
    }
}
