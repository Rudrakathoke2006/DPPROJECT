# Campus & Co. — JSP MVC frontend

A responsive university event-management frontend with forest-green and ivory styling, custom CSS artwork, and role-specific workspaces. No external fonts or image services are required.

## Run

Install JDK 17 or newer and ensure `java` and `javac` are on PATH. Open PowerShell in this `frontend` folder:

```powershell
.\run.ps1
```

Open **http://localhost:8080/app**. Stop the server with Ctrl+C.

Compiled classes and dependencies are included for offline startup. To rebuild after editing frontend source, run `.\build.ps1` first. No Maven, Node, database, or separate Tomcat installation is required. Use `.\run.ps1 -Port 8090` if port 8080 is occupied.

If Windows blocks local scripts, use `powershell -NoProfile -ExecutionPolicy Bypass -File .\run.ps1`. This invocation affects only that process, not the saved execution policy.

## Existing sample accounts

| Role | Username | Password |
| --- | --- | --- |
| Faculty | rahul | rahul123 |
| Student | aarav | aarav123 |
| Student | meera | meera123 |
| HOD | priya | priya123 |
| Dean | anjali | anjali123 |
| Admin | admin | admin123 |

These accounts come from the original `data/users.csv`; none were changed. Start with **rahul** to explore event management.

## Features

- Login, logout, role-aware dashboard and real data counts.
- Event catalogue with combined search, category and status filters.
- Monthly calendar and event details.
- Event creation, editing, undo, and faculty duplication.
- Faculty → HOD → Dean approvals using the original backend queue. Each submission processes one request. Admin can view approvals but cannot decide for another role.
- Student registration, duplicate prevention, personal registrations and notifications.
- Venue directory and category-based allocation of venue, equipment and support staff.
- Event updates through the existing Observer/Bridge services.
- Faculty event-preparation reports and coordination activity log.
- Mobile navigation, empty states, keyboard focus, server-side role checks, CSRF validation and HTML escaping.

## Preservation and data

**Every original file is unchanged.** All additions live under `frontend/`. The existing Java files are compiled directly without edits.

The backend uses relative CSV paths. The launcher runs from `frontend/runtime/`, seeding `runtime/data/` only when each file is absent. Web actions update this separate copy; original `../data/` files stay untouched. Runtime data survives restarts. Use `-RuntimeDirectory demo-new` to start another fresh copy. Do not run two server processes against the same runtime directory.

## MVC structure

- **Model:** unchanged original Java models, factories, services and CSV handlers in `../src/`.
- **Controller:** new `src/main/java/campus/web/CampusServlet.java` handles HTTP, validation, sessions and adapters to original services.
- **Presentation model:** `ViewData.java` reads CSV data and escapes display values.
- **Views:** JSP files under `src/main/webapp/WEB-INF/views/` plus `assets/app.css` and `assets/app.js`.
- **Runtime:** `Launcher.java` starts embedded Tomcat.

New Java classes are web integration only, not replacements for existing backend files. Scanner-only services receive validated form values through an in-memory Scanner. CSV operations are serialized by the controller. The preparation service's console output is captured for its web report.

Existing email/SMS behavior is preserved; the frontend does not add an external delivery provider. Venue cards now connect to the existing smart venue selection service, validating capacity, budget, equipment and date conflicts. The detailed snapshot-history viewer remains in the original console application. The web UI includes edit/undo and coordination history.

## Verification

`verify_http.py` is an optional Python integration suite. Run it only against disposable test data:

```powershell
.\run.ps1 -Port 8082 -RuntimeDirectory qa-fresh
# Second terminal, in frontend:
python -u .\verify_http.py http://127.0.0.1:8082/app
```

See `VERIFICATION.md` for delivery test results and original-file preservation checks.

## Dependencies

Bundled JARs: Apache Tomcat 10.1.60 (core, Jasper, EL, annotations) and Eclipse ECJ 3.33.0. Missing JARs are downloaded from Maven Central by `build.ps1`; license notices are included in the JARs. [Official Tomcat documentation](https://tomcat.apache.org/tomcat-10.1-doc/).

The server binds to loopback for local demonstration. Original authentication, CSV storage formats and business rules remain unchanged.

