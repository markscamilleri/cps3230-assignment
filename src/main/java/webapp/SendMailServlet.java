package webapp;

import system.MessagingSystem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class FirstServlet
 */
@WebServlet("/sendmail")
public class SendMailServlet extends HttpServlet {

    private final MessagingSystem messagingSystem;

    public SendMailServlet(MessagingSystem messagingSystem) {
        this.messagingSystem = messagingSystem;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        final Cookie idCookie = Utils.findCookie(request.getCookies(), CookieNames.ID.name());
        final Cookie skCookie = Utils.findCookie(request.getCookies(), CookieNames.SKEY.name());

        if (idCookie == null || skCookie == null) {
            response.sendRedirect("/login");
        } else {
            final String id = idCookie.getValue();
            final String sessionKey = skCookie.getValue();

            final boolean hasMessages = messagingSystem.agentHasMessages(sessionKey, id);
            final String mailboxMessage = hasMessages ? "You have new messages" : "You have no new messages";

            response.getWriter().println("" +
                    "<h1>Mailbox</h1>\n" +
                    "    <div id=\"mailboxBlock\" class=\"inbox\">" +
                    "    <p id=\"mailboxMessagae\">" + mailboxMessage + "</p>\n" +
                    "    <button id=\"consumeMessage\" " + (hasMessages ? "" : "disabled") + "> Get Next Message </button>" +
                    "</div><br>\n" +
                    "<div id=\"composeFormBlock\" class=\"compose\">" +
                    "    <form id=\"composeForm\" method=\"POST\" action=\"/sendmail\"/>" +
                    "    <input class=\"form-input\" type=\"text\" name=\"destination\" id=\"destination\" placeholder=\"To Agent ID:\" />" +
                    "    <textarea class=\"form-input\" name=\"messageBody\" id=\"messageBody\" placeholder=\"Message Body\" rows=\"2\" cols=\"70\"></textarea>" +
                    "    <button id=\"submit\" type=\"submit\">Submit</button>" +
                    "</div>"
            );
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final Cookie idCookie = Utils.findCookie(request.getCookies(), CookieNames.ID.name());
        final Cookie skCookie = Utils.findCookie(request.getCookies(), CookieNames.SKEY.name());

        if (idCookie == null || skCookie == null) {
            response.sendRedirect("/login");
        } else {
            final String id = idCookie.getValue();
            final String sessionKey = skCookie.getValue();

            final String destination = request.getParameter("destination");
            final String message = request.getParameter("messageBody");

            messagingSystem.sendMessage(sessionKey, id, destination, message);
        }
    }

}