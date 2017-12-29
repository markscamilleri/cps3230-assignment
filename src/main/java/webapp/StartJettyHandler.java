package webapp;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import system.MessagingSystem;

public class StartJettyHandler {

    private static final int PORT_NUMBER = 8080;
    
    public static void startServer() {

        final MessagingSystem messagingSystem = new MessagingSystem();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Server server = new Server(PORT_NUMBER);
                try {
                    server.getConnectors()[0].getConnectionFactory(HttpConnectionFactory.class);

                    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                    context.setContextPath("/");
                    server.setHandler(context);

                    context.addServlet(new ServletHolder(new LoginServlet(messagingSystem)),"/login/*");
                    context.addServlet(new ServletHolder(new SendMailServlet(messagingSystem)), "/sendmail/*");
                    
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