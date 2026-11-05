package campus.web;

import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

/** Local JSP container. Run from the isolated runtime directory, never the original project root. */
public final class Launcher {
    public static void main(String[] args) throws Exception {
        Tomcat server = new Tomcat();
        server.setBaseDir(new File("tomcat").getAbsolutePath());
        server.setPort(args.length > 1 ? Integer.parseInt(args[1]) : 8080);
        server.getConnector().setProperty("address", "127.0.0.1");
        Context context = server.addWebapp("", new File(args[0]).getAbsolutePath());
        context.setParentClassLoader(Launcher.class.getClassLoader());
        Tomcat.addServlet(context, "campus", new CampusServlet());
        context.addServletMappingDecoded("/app", "campus");
        context.setSessionTimeout(30);
        server.start();
        if (!server.getConnector().getState().isAvailable()) {
            server.stop(); server.destroy();
            throw new IllegalStateException("HTTP connector failed to start. Check port availability and local network permissions.");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { try { server.stop(); server.destroy(); } catch (Exception ignored) { } }));
        System.out.println("Campus & Co. is ready: http://localhost:" + server.getConnector().getPort() + "/app");
        server.getServer().await();
    }
}
