package webapp;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class StartJettyHandler {

    public static void startServer() {

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Server server = new Server(8080);
                try {
                    server.getConnectors()[0].getConnectionFactory(HttpConnectionFactory.class);

                    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                    context.setContextPath("/");
                    server.setHandler(context);

                    context.addServlet(new ServletHolder(new Serverlet()),"/*");
                    //context.addServlet(new ServletHolder(new Serverlet("Buongiorno Mondo")),"/it/*");
                    //context.addServlet(new ServletHolder(new Serverlet("Bonjour le Monde")),"/fr/*");

                    server.start();
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public static void main(String[] args) {
        startServer();
    }
}