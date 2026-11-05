# Verification — 15 October 2026

Executed on Windows with Oracle JDK 23.0.1, compiling all Java sources with `--release 17`, and embedded Tomcat 10.1.60.

- Compilation: successful; only the existing Tomcat deprecated-API note.
- Root PowerShell launcher: compiled and started the real JSP server successfully.
- Six backend suites: Member1BuilderPrototypeTest, FullRegressionTest, MediatorRegressionTest, StrategyIntegrationTest, StrategyConsoleRegressionTest, WebDataRegressionTest — exit 0.
- Console Main: real faculty login, view events, logout and exit passed.
- 55 HTTP workflow assertions passed on an empty reset runtime, and passed again after a second complete reset.
- Original sample-data suite: all 46 checks in frontend/verify_fixes.py passed.
- Restart: event details/allocation and student notification visible after restart; all CSV SHA-256 values unchanged.
- Reset: all eight event-dependent CSVs retained only headers; users.csv and venues.csv unchanged byte-for-byte; backup hashes exactly matched pre-reset data.
- Post-reset restart: neither test events nor original samples returned; complete workflow succeeded again.
- Live browser: faculty login; Resources form; 50 attendees, 16000 budget, no required equipment; Capacity First -> Open Air Theatre, Budget First -> Innovation Lab, Equipment First -> Main Auditorium. Recommendation explanations and automatic dropdown selection visibly changed. No JavaScript errors reported. Screenshot: strategy-browser.png.
- HTTP eligibility checks: insufficient capacity/budget/equipment, same-date booking exclusion, allocation-time booking conflict, manual override, duplicate registration/ID, CSRF and role access.
- All empty and populated JSP routes were requested, including event/edit/clone views.
- CSV regression: UTF-8 BOM plus whitespace header maps to the actual event ID; missing IDs and invalid saves are rejected before URL encoding.

Logs: backend-results.txt, workflow-results.txt, server-results.txt. Test writes were confined to disposable runtimes. Existing application data and CSS were preserved.

The original ZIP had clean event headers, so the exact historical corrupt runtime file was unavailable. The parser defect was identified from code and exercised with a malformed-header fixture. No claim is made that unrelated malformed/corrupt CSV formats can be automatically repaired.

Email and SMS remain the original simulated providers. The current browser check covers the Resources interaction and layout; it is not a comprehensive mobile/accessibility audit.

## Fresh ZIP extraction smoke test

The complete archive was extracted into a separate folder. Its root launcher compiled and started on port 8094, faculty login and five JSP pages passed, and event cards rendered correctly with an actual BOM/whitespace header fixture. Reset correctly refused the live runtime. After stopping it, reset completed and the extracted `powershell -File run.ps1 -Console` launcher passed login, viewing the empty events list, logout and exit. Console input was supplied to the PowerShell process stdin; piping text into a PowerShell script invocation does not forward that pipeline to Java and is not the supported console launch command.
