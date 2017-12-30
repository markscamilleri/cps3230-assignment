package webapp;

import system.MessagingSystem;
import system.StatusCodes;

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
            
            String sendingMessageStatusText = "";
            Cookie statusCookie = Utils.findCookie(request.getCookies(), CookieNames.MESSAGE_SENDING_STATUS.name());
            if (statusCookie != null){
                if(statusCookie.getValue().equals(StatusCodes.OK.name()))
                    sendingMessageStatusText = "Message Sent Successfully";
                else if (statusCookie.getValue().equals(StatusCodes.AGENT_DOES_NOT_EXIST.name()))
                    sendingMessageStatusText = "That agent does not exist";
                else if (statusCookie.getValue().equals(StatusCodes.MESSAGE_CONTAINS_BLOCKED_WORD.name()))
                    sendingMessageStatusText = "Message contained one or more blocked words that have been left out.";
                else if (statusCookie.getValue().equals(StatusCodes.MESSAGE_LENGTH_EXCEEDED.name()))
                    sendingMessageStatusText = "Message was longer than 140 characters. Only the first 140 were sent.";
                else if (statusCookie.getValue().equals(StatusCodes.FAILED_TO_ADD_TO_MAILBOX.name()))
                    sendingMessageStatusText = "Failed to add the message to the destination mailbox";
                else if (statusCookie.getValue().equals(StatusCodes.GENERIC_ERROR.name()))
                    sendingMessageStatusText = "An error occured when sending your message";
                // these should not happen, since the idCookie or skCookie should be deleted.
                else if (statusCookie.getValue().equals(StatusCodes.SESSION_KEY_UNRECOGNIZED) ||
                                 statusCookie.getValue().equals(StatusCodes.AGENT_NOT_LOGGED_IN.name()) ||
                                 statusCookie.getValue().equals(StatusCodes.SESSION_KEY_INVALID_LENGTH.name()))
                    response.sendRedirect("/login");
                
                    
    
            }
            
            final boolean hasMessages = messagingSystem.agentHasMessages(sessionKey, id);
            final String mailboxMessage = hasMessages ? "You have new messages" : "You have no new messages";

            response.getWriter().println("" +
                    "<h1>Mailbox</h1>\n" +
                    "<h2>Hello Agent "+ id  + "</h2>\n" +
                    "<p class=\"notification\">"+ sendingMessageStatusText +"</p>" +
                    "    <div id=\"mailboxBlock\" class=\"inbox\">\n" +
                    "    <p id=\"mailboxMessagae\">" + mailboxMessage + "</p>\n" +
                    "    <button id=\"consumeMessage\" " + (hasMessages ? "" : "disabled") + "> Get Next Message </button>\n" +
                    "</div><br>\n" +
                    "<div id=\"composeFormBlock\" class=\"compose\">" +
                    "    <form id=\"composeForm\" method=\"POST\" action=\"/sendmail\"/>" +
                    "    <input class=\"form-input\" type=\"text\" name=\"destination\" id=\"destination\" placeholder=\"To Agent ID:\" />" +
                    "    <textarea class=\"form-input\" name=\"messageBody\" id=\"messageBody\" placeholder=\"Message Body (140 characters)\" rows=\"2\" cols=\"70\"></textarea>" +
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

            StatusCodes status = messagingSystem.sendMessage(sessionKey, id, destination, message);
            
            switch(status) {
                case SESSION_KEY_UNRECOGNIZED:
                case AGENT_NOT_LOGGED_IN:
                case SESSION_KEY_INVALID_LENGTH:
                    idCookie.setMaxAge(0);
                    idCookie.setValue(null);
                    skCookie.setMaxAge(0);
                    skCookie.setValue(null);
                    break;
    
                default:
                    response.addCookie(new Cookie(CookieNames.MESSAGE_SENDING_STATUS.name(), status.name()));
            }
            response.sendRedirect("/sendmail");
        }
    }

}