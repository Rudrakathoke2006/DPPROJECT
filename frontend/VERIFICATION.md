# Verification results

- All 37 HTTP integration checks passed on 12 October 2026.
- Every JSP view rendered successfully on embedded Tomcat 10.1.60.
- Verified login, invalid credentials, logout, create, duplicate ID rejection, edit, undo, clone, Faculty/HOD/Dean approval routing, allocation, preparation reports, registration, duplicate prevention, notifications, CSRF and role restrictions.
- Desktop and mobile layouts inspected in the browser. Event-category filtering and calendar month navigation worked; no JavaScript console errors were observed.
- SHA-256 verification: all 260 original files match the supplied ZIP byte-for-byte, including all 120 original Java source files.
- Test actions used isolated frontend/qa-* data directories; those directories are excluded from the delivery.
- The package includes original project files, new frontend source, compiled classes and dependency JARs. It excludes test runtime files and machine-specific compiler argument lists.

## Usability fixes — 12 October 2026
- All 46 integration checks passed, including selected-venue allocation, insufficient-capacity rejection, mandatory notification choice, and persisted SMS preference.
- Browser confirmed visible top-bar logout, scrollable sidebar, and three initially unselected required channel choices.
- Existing runtime data preserved when restarting the live app.
