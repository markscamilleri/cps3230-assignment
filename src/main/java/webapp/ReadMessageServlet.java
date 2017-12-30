package webapp;

import system.Message;
import system.MessagingSystem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/readmessage")
public class ReadMessageServlet extends HttpServlet {

    private final MessagingSystem messagingSystem;

    public ReadMessageServlet(MessagingSystem messagingSystem) {
        this.messagingSystem = messagingSystem;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        final Cookie idCookie = Utils.findCookie(request.getCookies(), CookieNames.ID.name());
        final Cookie skCookie = Utils.findCookie(request.getCookies(), CookieNames.SESSION_KEY.name());

        if (idCookie == null || skCookie == null) {
            response.sendRedirect("/login");
        } else {
            final String id = idCookie.getValue();
            final String sessionKey = skCookie.getValue();

            if (messagingSystem.agentHasMessages(sessionKey, id)) {
                final Message message = messagingSystem.getNextMessage(sessionKey, id);
                response.getWriter().println("" +
                        "<h1>Latest Message</h1>" +
                        "<p>" +
                        "    <b>From</b>: Agent " + message.getSourceAgentId() + "<br>" +
                        "    <b>To</b>: Agent " + message.getTargetAgentId() + "<br>" +
                        "    <b>Timestamp</b>: " + message.getTimestamp() + "<br>" +
                        "    <b>Message</b>: " + message.getMessage() + "<br>" +
                        "</p>" +
                        "<a href=\"/readmessage\"><button id=\"consumeAnother\"> Consume another message </button></a><br>" +
                        "<a href=\"/sendmail\"><button id=\"backToSendMail\"> Go back </button></a>"
                );
            } else {
                response.getWriter().println("" +
                        "<h1>Latest Message</h1>" +
                        "<p>You have no new messages</p>" +
                        "<a href=\"/readmessage\"><button id=\"consume\"> Retry </button></a><br>" +
                        "<a href=\"/sendmail\"><button id=\"backToSendMail\"> Go back </button></a>"
                );
            }
        }
    }
}
