# Campus & Co. — complete Java Design Patterns project

## Run on Windows

Install a **JDK 17 or newer** (a JRE is insufficient). Extract the entire ZIP first. Open PowerShell in the extracted `Campus-Co-Final` folder:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1
```

Open http://localhost:8080/app. Stop the server with **Ctrl+C** before resetting or running the console on the same data.

The launcher compiles all Java source every time, uses the included Tomcat/Jasper dependency JARs, resolves JAVA_HOME or an installed JDK, and reports compilation/startup errors. No Maven or database installation is required. An internet connection is needed only if an included dependency JAR has been removed.

```powershell
# Alternate port
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 -Port 8081
# Console application, sharing the same runtime data
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 -Console
# Compile only
powershell -NoProfile -ExecutionPolicy Bypass -File .\frontend\build.ps1
```

## Reset event data

Stop Campus & Co. first, then run:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\reset.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1
```

The actual default data location is `frontend/runtime/data`, resolved relative to the project, independent of the shell's working directory. Reset backs up the whole data directory into `frontend/runtime/backups/<timestamp>/data`, then keeps only the header of every CSV except **users.csv** and **venues.csv**. Accounts, passwords, roles and venue definitions remain byte-for-byte unchanged. This clears events, approvals, details, history, registrations, resources/allocations, notifications and coordination records, including any additional CSV record files. Headers are written as UTF-8 without BOM.

An initialization marker prevents missing event CSVs from being reseeded after reset. Existing runtime files are never overwritten with sample files on startup. A runtime lock blocks concurrent launcher/reset operations. Use these scripts instead of running a second Java process directly against live data.

Custom directories are supported; use the same value with both scripts (root script relative paths are relative to the project):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 -RuntimeDirectory demo-runtime -Port 8081
# Stop that server first
powershell -NoProfile -ExecutionPolicy Bypass -File .\reset.ps1 -RuntimeDirectory demo-runtime
```

To restore a backup, stop the application and copy all CSV files from the chosen backup's `data` folder into the runtime `data` folder. Backups are never loaded automatically.

## Login and workflow

Existing accounts remain in `data/users.csv` and `frontend/runtime/data/users.csv`.

| Role | Username | Password |
|---|---|---|
| Faculty | rahul | rahul123 |
| HOD | priya | priya123 |
| Dean | anjali | anjali123 |
| Student | aarav | aarav123 |
| Admin | admin | admin123 |

1. Sign in as Faculty, create an event with a unique explicit Event ID. The existing project uses user-entered event IDs, not an automatic ID generator.
2. Process approvals: department events finish at Faculty; inter-department events continue to HOD; university events continue to HOD and Dean.
3. On Resources, select an approved event, attendees, budget and optional semicolon-separated equipment. Choose Capacity First, Budget First or Equipment First. The recommendation and reason update automatically; the recommended venue is selected automatically when changing strategy.
4. You may select another venue manually or click a venue card. Confirmation revalidates eligibility and date bookings before allocating category equipment and staff through the existing Abstract Factory.
5. Sign in as a student, register with INAPP, EMAIL or SMS preference, then publish an event update as faculty to see the saved notification.

Capacity First maximizes seating, Budget First minimizes booking cost, Equipment First maximizes additional equipment. All require sufficient capacity, affordable cost, mandatory equipment and availability. Same-date allocations and inventory unavailable dates exclude venues. Strategies may legitimately agree when the same venue wins both rankings. For 50 attendees, budget 16000, no equipment and an unbooked date, the supplied inventory yields Open Air Theatre / Innovation Lab / Main Auditorium respectively.

Email and SMS retain the original project's simulated console delivery plus CSV history; this project does not configure external mail/SMS providers.

## Architecture and error fix

The JSP/servlet MVC frontend delegates to the existing Java services. All original pattern implementations remain: Singleton, Factory Method, Abstract Factory, Builder, Prototype, Proxy, Chain of Responsibility, Observer, Bridge, Mediator, Strategy, Template Method and Memento. Styling is unchanged.

The old presentation CSV reader used header bytes verbatim. A UTF-8 BOM or whitespace on `eventId` made the map key differ, causing `card.jsp` and other URL builders to pass null to URLEncoder. The reader now normalizes BOM/whitespace for every CSV, validates the event schema and rejects genuinely missing IDs with a diagnostic. Saving rejects missing/invalid IDs, and an empty event file gets its header initialized. Valid IDs remain unchanged. The supplied original CSV headers were clean; the malformed-header regression reproduces the mapping defect rather than assuming the historical runtime file is still present.

## Executed verification

See `tests/VERIFICATION.md` and the included result logs. Tests use disposable runtimes; production data was not reset or modified by tests.

```powershell
# Backend regressions (requires Python 3)
powershell -NoProfile -ExecutionPolicy Bypass -File .\frontend\build.ps1
. .\runtime-common.ps1
python .\tests\backend.py "$(Find-CampusJdk)/bin/java.exe"

# Fresh HTTP workflow: two terminals, requires Python 3
powershell -NoProfile -ExecutionPolicy Bypass -File .\reset.ps1 -RuntimeDirectory qa-manual
powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 -RuntimeDirectory qa-manual -Port 8091
# In the second terminal:
python .\tests\workflow.py http://127.0.0.1:8091/app
```

The legacy sample-data tests remain in `frontend/verify_http.py` and `frontend/verify_fixes.py`; they require a separate new seeded runtime. Original console documentation is retained in `docs/ORIGINAL-CONSOLE-README.md`.
