package webapp;

import system.MessagingSystem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class FirstServlet
 */
@WebServlet("/sendmail")
public class SendMailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private final MessagingSystem messagingSystem;
    
    /**
     * Constructor.
     */
    public SendMailServlet(MessagingSystem messagingSystem) {
        this.messagingSystem = messagingSystem;
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.setContentType("text/html");
        String id = request.getParameter("id");
        String sessionKey = request.getParameter("sessionKey");
        
        
            
        boolean hasMessages = messagingSystem.agentHasMessages(sessionKey, id);
        
        String mailboxMessage = hasMessages ? "You have new messages" : "You have no new messages";
            
        response.getWriter().println(
                "<h1>Mailbox</h1>\n" +
                        "<div id=\"mailboxBlock\" class=\"inbox\">"+
                            "<p id=\"mailboxMessagae\">" + mailboxMessage + "</p>\n"+
                            "<button id=\"consumeMessage\" "+ (hasMessages?"":"disabled") +"> Get Next Message </button>"+
                        "</div>\n"+
                        "<div id=\"composeFormBlock\" class=\"compose\">"+
                            "<form id=\"composeForm\" method=\"POST\" action=\"/mailbox\"/>"+
                            "<input class=\"form-input\" type=\"text\" name=\"destination\" id=\"destination\" placeholder=\"To Agent ID:\" />"+
                            "<textarea class=\"form-input\" name=\"messageBody\" id=\"messageBody\" placeholder=\"Message Body\"/>"+
                            "<button id=\"submit\" type=\"submit\">Submit</button>" +
                        "</div>"
                        
        );
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("/login");
    }
    
}