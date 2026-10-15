SMART UNIVERSITY EVENT & FEST MANAGEMENT SYSTEM
================================================
A Java console application for planning, approving, coordinating, and managing university events. The application stores its operational data in CSV files, making the records easy to inspect and maintain without a database server.

1. PROJECT OVERVIEW
-------------------
The system supports students, faculty members, Heads of Department (HODs), the Dean, and an administrator. It provides event creation and editing, role-based approval, venue and resource allocation, student registration, notifications, event history, and coordination status reporting.

2. MAIN FEATURES
----------------
- Role-based login for Student, Faculty, HOD, Dean, and Admin accounts.
- Event creation with event type, event level, date, venue, budget, capacity, duration, equipment, and special instructions.
- Approval workflow based on event level:
  * DEPARTMENT: Faculty makes the final decision.
  * INTER_DEPARTMENT: Faculty forwards the request to the HOD, who makes the final decision.
  * UNIVERSITY: Faculty forwards to the HOD, who forwards to the Dean for the final decision.
- Event editing with saved snapshots and undo support.
- Event cloning to reuse event details while creating a separate pending event.
- Venue selection using capacity-first, budget-first, or equipment-first strategies.
- Resource allocation through event-type-specific resource families.
- Student registration for approved events, with EMAIL, SMS, or INAPP notification-channel selection.
- Event update notifications for registered students.
- Coordination status showing approval, allocation, registration, and notification information.
- CSV-based persistence across application runs.

3. DESIGN PATTERNS
------------------
The project demonstrates 13 design patterns. The following source files are the main entry points for each pattern.

1. Singleton — src/singleton/FestManagementSystem.java
   Maintains the shared event-management system instance and its coordinated services.
2. Proxy — src/proxy/EventServiceProxy.java
   Controls access to event services through authentication and role checks.
3. Factory Method — src/factory/EventCreator.java and concrete event creators
   Creates event objects for technical, cultural, sports, and workshop events.
4. Chain of Responsibility — src/chain/ApprovalManager.java and approval handlers
   Routes approval requests through Faculty, HOD, and Dean according to event level.
5. Abstract Factory — src/abstractfactory/ResourceFactory.java and concrete factories
   Creates compatible venue, equipment, and support-staff objects for an event category.
6. Observer — src/observer/EventUpdateSubject.java and observer classes
   Notifies registered observers when event updates are published.
7. Bridge — src/bridge/Notification.java and notification sender classes
   Separates notification content from delivery channels such as email, SMS, and in-app messages.
8. Builder — src/builder/EventBuilder.java
   Builds event objects step by step and applies validation/default values.
9. Prototype — src/prototype/CloneEventService.java and src/prototype/EventCloner.java
   Creates new events from existing event templates.
10. Memento — src/memento/EventEditService.java and src/memento/EventHistoryCaretaker.java
    Saves event snapshots and supports undoing edits.
11. Strategy — src/strategy/VenueSelectionStrategy.java and concrete strategies
    Makes venue ranking interchangeable according to capacity, budget, or equipment priorities.
12. Template Method — src/template/EventPreparationTemplate.java and concrete preparation classes
    Defines a common event-preparation sequence with category-specific steps.
13. Mediator — src/mediator/UniversityEventCoordinator.java
    Coordinates approval, resource, registration, and notification modules and records lifecycle activity.

4. TECHNOLOGY AND STORAGE
-------------------------
- Language: Java
- Interface: console/menu-driven interaction
- Persistence: comma-separated values (CSV)
- External database: not required
- Java collections may be used internally by implementation classes; CSV files are the persistent data store.

5. PROJECT STRUCTURE
--------------------
src/       Java source files, grouped by design pattern and application responsibility
 data/     CSV records used by the application
 build/    Compiled classes generated when running the build/test command
README.txt Project overview, setup, and usage instructions
run_presentation_tests.sh   Compile and run the automated presentation test suites (Linux/macOS)
run_presentation_tests.bat  Compile and run the automated presentation test suites (Windows)

6. CSV FILE REFERENCE
---------------------
- users.csv: user accounts, login names, passwords, and roles.
- events.csv: core event details and current workflow status.
- event_details.csv: extended event description, budget, capacity, duration, equipment, and instructions.
- approvals.csv: approval request and decision records.
- venues.csv: venue capacity, cost, available equipment, and unavailable dates.
- resources.csv: resources allocated to approved events.
- registrations.csv: student registrations and selected notification channels.
- notifications.csv: notifications sent to recipients.
- event_history.csv: Base64-encoded event snapshots used by the Memento implementation.
- coordination_log.csv: event lifecycle signals and coordination outcomes.

The sample data includes approved and pending events, multiple event categories, role accounts, venue inventory, registrations, notifications, and coordination records. The CSV headers are part of each file's required schema; fields differ where the files serve different purposes.

7. SAMPLE LOGIN ACCOUNTS
------------------------
These sample accounts are provided in data/users.csv for local demonstration:

Role       Username   Password
Student    aarav      aarav123
Student    meera      meera123
Faculty    rahul      rahul123
HOD        priya      priya123
Dean       anjali     anjali123
Admin      admin      admin123

These credentials are demonstration data, not suitable for production use.

8. BUILD AND RUN
----------------
Prerequisite: install a JDK and ensure both java and javac are available from the terminal. Run commands from the project root directory.

Compile all Java source files:

  mkdir -p build
  find src -name '*.java' -print > build/sources.list
  javac -encoding UTF-8 -d build @build/sources.list

Run the application:

  java -cp build Main

On Windows, create the build directory, compile the Java files with javac, and then run `java -cp build Main` from the project root. The supplied `run_presentation_tests.bat` handles compilation for the automated tests.

The application is designed to be launched from the project root because it reads and writes files under the relative `data/` directory. Keep the CSV files closed in spreadsheet applications while the program is running.

9. PRESENTATION TESTS
---------------------
Run all automated regression suites with one command:

Linux/macOS:
  ./run_presentation_tests.sh

Windows:
  run_presentation_tests.bat

The runner compiles the project and executes the Builder/Prototype checks, full regression checks, Mediator lifecycle checks, Strategy integration checks, and Strategy console regression checks. It prints the purpose and result of each suite and finishes with a pass/fail summary.

Each automated suite runs in a separate temporary data workspace. The runner resets the relevant test CSVs to their expected initial state, so test-created events and history do not contaminate the sample data used by the menu-driven application. Interactive member demonstration classes remain available in src/ for guided, manual demonstrations; they are not launched automatically because they require user input.

10. IMPORTANT USAGE NOTES
-------------------------
- Launch `Main` only once per application session. Use the main menu to log in or exit.
- Choosing the logout option in a role menu returns to the main login menu.
- Only approved events are eligible for student registration and resource allocation.
- Event approval authority depends on the event level as described above.
- Keep a backup of the `data/` directory before manually editing CSV records.
- This is an academic demonstration application. Passwords are stored as plain text in the sample CSV file; do not use real or sensitive credentials.
