package webapp;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;

public class StartJettyHandler {

    public static void startServer() {

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Server server = new Server(8080);
                try {
                    server.getConnectors()[0].getConnectionFactory(HttpConnectionFactory.class);
                    server.setHandler(new HttpRequestHandler());

                    server.start();
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }
}