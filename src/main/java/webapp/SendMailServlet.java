package webapp;

import system.MessagingSystem;
import system.StatusCodes;

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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");

        final Cookie idCookie = Utils.findCookie(request.getCookies(), CookieNames.AGENT_ID.name());
        final Cookie skCookie = Utils.findCookie(request.getCookies(), CookieNames.SESSION_KEY.name());

        if (idCookie == null || skCookie == null) {
            response.sendRedirect("/register");
        } else {
            final String id = idCookie.getValue();
            final String sessionKey = skCookie.getValue();

            String sendingMessageStatusText = "";
            final Cookie statusCookie = Utils.findCookie(request.getCookies(), CookieNames.MESSAGE_SENDING_STATUS.name());
            if (statusCookie != null) {

                // Get cookie value and delete the cookie
                final String statusValue = statusCookie.getValue();
                Utils.deleteCookie(statusCookie, response);

                // Set the status message
                if (statusValue.equals(StatusCodes.OK.name())) {
                    sendingMessageStatusText = "Message Sent Successfully";
                } else if (statusValue.equals(StatusCodes.AGENT_DOES_NOT_EXIST.name())) {
                    sendingMessageStatusText = "That agent does not exist";
                } else if (statusValue.equals(StatusCodes.MESSAGE_LENGTH_EXCEEDED.name())) {
                    sendingMessageStatusText = "The message was not sent as it is longer than 140 characters";
                } else if (statusValue.equals(StatusCodes.FAILED_TO_ADD_TO_MAILBOX.name())) {
                    sendingMessageStatusText = "Failed to add the message to the destination mailbox";
                } else {
                    /*In the case of SESSION_KEY_UNRECOGNIZED, AGENT_NOT_LOGGED_IN, and
                    SESSION_KEY_INVALID_LENGTH, the user should have been logged out.*/
                    System.err.println("Unexpected statusCookie \"" + statusValue + "\" in SendMailServlet.");
                }
            }

            final boolean hasMessages = messagingSystem.agentHasMessages(sessionKey, id);
            final String mailboxMessage = hasMessages ? "You have new messages" : "You have no new messages";

            response.getWriter().println("" +
                    "<h1>Agent " + id + "'s Mailbox</h1>" +
                    "<hr>" +
                    Utils.getHrefButton("/logout", "logout", "Logout") +
                    "<hr>" +
                    "<div id=\"mailboxBlock\" class=\"inbox\">" +
                    "    <p id=\"mailboxMessagae\">" + mailboxMessage + "</p>" +
                    "    " + Utils.getHrefButton("/readmessage", "consumeMessage", "Get Next Message") +
                    "</div>" +
                    "<hr>" +
                    "<div id=\"composeFormBlock\" class=\"compose\">" +
                    "    <p class=\"notification\">" + Utils.getSpan("notif", sendingMessageStatusText) + "</p>" +
                    "    " + Utils.getPostForm("composeForm", "/sendmail") +
                    "    " + Utils.getInputField("destination", "To Agent ID:", true) + "<br>" +
                    "    " + Utils.getTextArea("messageBody", "Message Body (140 characters)", 2, 70) + "<br>" +
                    "    " + Utils.getSubmitButton("submit", "Send message") +
                    "</div>"
            );
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final Cookie idCookie = Utils.findCookie(request.getCookies(), CookieNames.AGENT_ID.name());
        final Cookie skCookie = Utils.findCookie(request.getCookies(), CookieNames.SESSION_KEY.name());

        if (idCookie == null || skCookie == null) {
            response.sendRedirect("/register");
        } else {
            final String id = idCookie.getValue();
            final String sessionKey = skCookie.getValue();

            final String destination = request.getParameter("destination");
            final String message = request.getParameter("messageBody");

            final StatusCodes status = messagingSystem.sendMessage(sessionKey, id, destination, message);

            switch (status) {
                case SESSION_KEY_UNRECOGNIZED:
                case AGENT_NOT_LOGGED_IN:
                case SESSION_KEY_INVALID_LENGTH:
                    Utils.deleteCookie(idCookie, response);
                    Utils.deleteCookie(skCookie, response);
                    break;

                default:
                    response.addCookie(new Cookie(CookieNames.MESSAGE_SENDING_STATUS.name(), status.name()));
            }
            response.sendRedirect("/sendmail");
        }
    }

}
