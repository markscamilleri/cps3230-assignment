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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");

        final Cookie idCookie = Utils.findCookie(request.getCookies(), CookieNames.AGENT_ID.name());
        final Cookie skCookie = Utils.findCookie(request.getCookies(), CookieNames.SESSION_KEY.name());

        if (idCookie == null || skCookie == null) {
            response.sendRedirect("/register");
        } else {
            final String id = idCookie.getValue();
            final String sessionKey = skCookie.getValue();

            if (messagingSystem.agentHasMessages(sessionKey, id)) {
                final Message message = messagingSystem.getNextMessage(sessionKey, id);
                response.getWriter().println("" +
                        "<h1>Latest Message</h1>" +
                        "<hr>" +
                        "<p>" +
                        "    <b>From</b>: Agent " + Utils.getSpan("from", message.getSourceAgentId()) + "<br>" +
                        "    <b>To</b>: Agent " + Utils.getSpan("to", message.getTargetAgentId()) + "<br>" +
                        "    <b>Timestamp</b>: " + Utils.getSpan("timestamp", "" + message.getTimestamp()) + "<br>" +
                        "    <b>Message</b>: " + Utils.getSpan("message", message.getMessage()) + "<br>" +
                        "</p>" +
                        Utils.getHrefButton("/readmessage", "consumeAnother", "Consume another message", false) + "<br>" +
                        Utils.getHrefButton("/sendmail", "backToMailbox", "Go back", false)
                );
            } else {
                response.getWriter().println("" +
                        "<h1>Latest Message</h1>" +
                        "<p>You have no new messages</p>" +
                        Utils.getHrefButton("/readmessage", "consume", "Try again", false) + "<br>" +
                        Utils.getHrefButton("/sendmail", "backToMailbox", "Go back", false)
                );
            }
        }
    }
}
