package campus.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import model.*;
import singleton.FestManagementSystem;
import controller.EventWorkflowController;
import builder.*;
import factory.EventCreatorProvider;

/** HTTP controller and adapter only. Original backend source files are compiled unchanged. */
public final class CampusServlet extends HttpServlet {
    private static final Object BACKEND_LOCK = new Object();
    private static final Set<String> PAGES = Set.of("dashboard","events","calendar","approvals","registrations","resources","notifications","activity","event","create","edit","clone");
    private final FestManagementSystem system = FestManagementSystem.getInstance();
    private static boolean role(User user, String... roles) { return user != null && Arrays.asList(roles).contains(user.getRole()); }
    private static void allow(User user, String... roles) { if (!role(user, roles)) throw new SecurityException("Your role does not have access to this action."); }
    private static String input(HttpServletRequest request, String name) {
        String value = Objects.toString(request.getParameter(name), "").trim();
        if (value.length()>2000 || value.contains("\n") || value.contains("\r")) throw new IllegalArgumentException("Please use a single line of at most 2,000 characters.");
        return value;
    }
    private static String required(HttpServletRequest request, String name) {
        String value=input(request,name); if(value.isBlank()) throw new IllegalArgumentException(ViewData.label(name)+" is required."); return value;
    }
    private static String date(HttpServletRequest request) {
        try { return LocalDate.parse(required(request,"date")).format(DateTimeFormatter.ofPattern("dd-MM-uuuu")); }
        catch (Exception ex) { throw new IllegalArgumentException("Choose a valid event date."); }
    }
    private static Scanner lines(String... values) { return new Scanner(String.join("\n",values)+"\n"); }
    private static void check(boolean success, String message) { if(!success) throw new IllegalArgumentException(message); }
    private static void token(HttpSession session) { if(session.getAttribute("csrf")==null) session.setAttribute("csrf", UUID.randomUUID().toString()); }
    private static void headers(HttpServletResponse response) {
        response.setHeader("Cache-Control","no-store"); response.setHeader("X-Content-Type-Options","nosniff");
        response.setHeader("Content-Security-Policy","default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; base-uri 'self'; form-action 'self'; frame-ancestors 'none'");
        response.setHeader("Referrer-Policy","same-origin");
    }
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        headers(response); response.setCharacterEncoding("UTF-8");
        HttpSession session=request.getSession(); token(session);
        User user=(User)session.getAttribute("user");
        if(user==null) { request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request,response); return; }
        String page=Objects.toString(request.getParameter("page"),"dashboard");
        if(!PAGES.contains(page)) { response.sendError(404); return; }
        if ((Set.of("create","edit").contains(page) && !role(user,"FACULTY","ADMIN")) || (page.equals("clone")&&!role(user,"FACULTY")) || (Set.of("approvals","resources","activity").contains(page)&&role(user,"STUDENT"))) { response.sendError(403);return; }
        synchronized(BACKEND_LOCK) {
            List<Map<String,String>> events=ViewData.rows("events");
            if(role(user,"STUDENT")) events.removeIf(e->!"APPROVED".equals(e.get("status")));
            events.sort(Comparator.comparing(e->ViewData.date(e.get("date"))));
            request.setAttribute("events",events);
            for(String file:List.of("event_details","approvals","registrations","resources","notifications","coordination_log","venues")) {
                List<Map<String,String>> rows=ViewData.rows(file);
                if(file.equals("notifications")) rows.removeIf(r->!user.getUsername().equalsIgnoreCase(r.get("recipient")));
                if(file.equals("registrations")&&role(user,"STUDENT")) rows.removeIf(r->!user.getUsername().equalsIgnoreCase(r.get("studentUsername")));
                if(file.equals("approvals")&&!role(user,"ADMIN")) rows.removeIf(r->!user.getUsername().equalsIgnoreCase(r.get("approverUsername")));
                request.setAttribute(file,rows);
            }
            if(Set.of("event","edit","clone").contains(page)) {
                Map<String,String> event=ViewData.find(events,"eventId",Objects.toString(request.getParameter("id"),""));
                if(event.isEmpty()) { response.sendError(404);return; }
                request.setAttribute("event",event);
            }
        }
        request.setAttribute("page",page);
        request.getRequestDispatcher("/WEB-INF/views/app.jsp").forward(request,response);
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8"); headers(response);
        HttpSession session=request.getSession(); token(session);
        if(!Objects.equals(session.getAttribute("csrf"),request.getParameter("csrf"))) { response.sendError(403,"Please reload the page and try again.");return; }
        String destination="/app";
        try {
            String action=required(request,"action");
            synchronized(BACKEND_LOCK) {
                if(action.equals("login")) {
                    User user=system.getAuthenticationManager().login(required(request,"username"),required(request,"password"));
                    check(user!=null,"The username or password is incorrect. Please try again.");
                    request.changeSessionId();session.setAttribute("user",user);session.setAttribute("csrf",UUID.randomUUID().toString());
                } else {
                    User user=(User)session.getAttribute("user");
                    if(user==null) throw new SecurityException("Please sign in again.");
                    if(action.equals("logout")) { session.invalidate();response.sendRedirect(request.getContextPath()+"/app");return; }
                    String id=input(request,"id");
                    if(Set.of("edit","clone","undo","register","allocate","publish","prepare","smart").contains(action)) check(system.getEventFileHandler().eventExists(id),"Event not found.");
                    String message;
                    switch(action) {
                        case "create": {
                            allow(user,"FACULTY","ADMIN");
                            String newId=required(request,"id");
                            check(newId.matches("[A-Za-z0-9_-]{1,40}"),"Event ID must contain only letters, numbers, underscores or hyphens.");
                            Event event=new EventBuilder(EventCreatorProvider.forChoice(required(request,"type")))
                                .withEventId(newId).withName(required(request,"name")).withOrganizer(user.getName()).withDate(date(request)).withVenue(required(request,"venue")).withLevel(required(request,"level"))
                                .withDescription(input(request,"description")).withBudget(Double.parseDouble(required(request,"budget"))).withCapacity(Integer.parseInt(required(request,"capacity"))).withDuration(Integer.parseInt(required(request,"duration"))).withEquipment(input(request,"equipment")).withSpecialInstructions(input(request,"instructions")).build();
                            check(new AdvancedEventCreationService().submitNewEvent(event),"Could not save the event. Check for a duplicate ID or a missing faculty approver.");
                            message="Your event is created and has been sent for faculty approval."; destination="/app?page=events"; break;
                        }
                        case "clone": {
                            allow(user,"FACULTY");
                            Event clone=new prototype.CloneEventService().prepareClone(id,required(request,"newId"),required(request,"name"),user.getName(),date(request),required(request,"venue"));
                            check(new AdvancedEventCreationService().submitNewEvent(clone),"Could not save the duplicate. Check the event ID.");
                            message="Event duplicated with a fresh approval request."; destination="/app?page=events";break;
                        }
                        case "edit": {
                            allow(user,"FACULTY","ADMIN");
                            check(new memento.EventEditService().edit(user,lines(id,required(request,"name"),date(request),required(request,"venue"),input(request,"description"),required(request,"budget"),required(request,"capacity"),required(request,"duration"),input(request,"equipment"),input(request,"instructions"))),"This event could not be edited. Events with allocated resources cannot be edited.");
                            message="Changes saved. The event has returned to faculty approval.";destination="/app?page=events";break;
                        }
                        case "undo": allow(user,"FACULTY","ADMIN");check(new memento.EventEditService().undoLast(user,lines(id)),"Cannot undo: no available history, or resources are allocated.");message="The last edit was restored. Faculty approval is required again.";break;
                        case "approve": {
                            allow(user,"FACULTY","HOD","DEAN");
                            Map<String,String> first=ViewData.rows("approvals").stream().filter(a->user.getUsername().equalsIgnoreCase(a.get("approverUsername"))&&user.getRole().equals(a.get("approverRole"))&&"PENDING".equals(a.get("status"))).findFirst().orElse(Map.of());
                            check(id.equals(first.get("eventId")),"The approval queue changed. Reload and review the next request.");
                            String decision=required(request,"decision");check(Set.of("yes","no").contains(decision),"Choose approve or reject.");
                            system.getApprovalManager().showPendingApprovals(user.getUsername(),user.getRole(),lines(decision,"skip"));
                            Map<String,String> after=ViewData.find(ViewData.rows("approvals"),"eventId",id);
                            check(!after.equals(first),"Approval could not be saved. Check that the next approver is configured.");
                            message="Decision saved. The existing approval chain has processed this request.";destination="/app?page=approvals";break;
                        }
                        case "register": {
                            allow(user,"STUDENT");String channel=required(request,"channel");check(Set.of("EMAIL","SMS","INAPP").contains(channel),"Choose a valid notification channel.");
                            check(!system.getRegistrationFileHandler().isRegistered(id,user.getUsername()),"You are already registered for this event.");
                            EventWorkflowController.registerForEvent(system,user,lines(id,channel));
                            check(system.getRegistrationFileHandler().isRegistered(id,user.getUsername()),"Registration could not be saved. Only approved events are open.");
                            message="You're on the list! Your event registration is confirmed.";destination="/app?page=registrations";break;
                        }
                        case "allocateVenue": allow(user,"FACULTY","ADMIN"); destination="/app?page=resources"; VenueAdapter.allocate(user,required(request,"id"),required(request,"venueId"),Integer.parseInt(required(request,"attendees")),Double.parseDouble(required(request,"budget")),input(request,"equipment"));message="Selected venue, equipment and support staff allocated.";break;
                        case "allocate": allow(user,"FACULTY","ADMIN");check(!system.getResourceFileHandler().resourceExists(id),"Resources are already allocated.");EventWorkflowController.allocateResources(system,lines(id));check(system.getResourceFileHandler().resourceExists(id),"Only approved events can receive resources.");message="Venue, equipment and support staff allocated.";destination="/app?page=resources";break;
                        case "publish": allow(user,"FACULTY","ADMIN","HOD","DEAN");check(system.getEventFileHandler().isEventApproved(id),"Updates are available for approved events only.");int count=system.getEventCoordinator().eventUpdated(id,required(request,"message"));message="Update published to "+count+" registered student(s).";break;
                        case "prepare": {
                            allow(user,"FACULTY");
                            // The original preparation service exposes console output, so adapt it under the shared lock.
                            ByteArrayOutputStream output=new ByteArrayOutputStream();PrintStream original=System.out;
                            try(PrintStream capture=new PrintStream(output,true,"UTF-8")) { System.setOut(capture);new template.EventPreparationService().runConsoleFlow(lines(id)); }
                            finally { System.setOut(original); }
                            session.setAttribute("report",output.toString("UTF-8"));message="Event preparation check completed.";break;
                        }
                        default: throw new IllegalArgumentException("Unknown action.");
                    }
                    session.setAttribute("flash",message);session.setAttribute("flashType","success");
                    if(destination.equals("/app")&&!id.isBlank()) destination="/app?page=event&id="+java.net.URLEncoder.encode(id,java.nio.charset.StandardCharsets.UTF_8);
                }
            }
        } catch(SecurityException ex) { response.sendError(403,ex.getMessage());return; }
          catch(IllegalArgumentException ex) { session.setAttribute("flash",ex instanceof NumberFormatException?"Enter valid numbers for budget, capacity and duration.":ex.getMessage());session.setAttribute("flashType","error"); }
          catch(Exception ex) { getServletContext().log("Frontend action failed",ex);session.setAttribute("flash","The operation could not be completed. Please try again.");session.setAttribute("flashType","error"); }
        response.sendRedirect(request.getContextPath()+destination);
    }
}
