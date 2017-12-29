package webapp;

import system.Agent;
import system.MessagingSystem;
import system.Supervisor;
import system.SupervisorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class FirstServlet
 */
@WebServlet("/mailbox")
public class MailboxServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static final MessagingSystem messagingSystem = new MessagingSystem();
    
    /**
     * Default constructor.
     */
    public MailboxServlet() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.setContentType("text/html");
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        
        Supervisor supervisor = new SupervisorImpl();
        
        Agent _007 = new Agent(id, name, supervisor, messagingSystem);
        
        if (_007.login()) {
            String sessionKey = _007.getSessionKey();
            
            String mailboxMessage = messagingSystem.agentHasMessages(sessionKey, id) ? "You have new messages" : "You have no new messages";
            
            response.getWriter().println(
                    "<h1>Mailbox</h1>\n" +
                            "<p>" + mailboxMessage + "</p>\n"
            );
        }
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.getWriter().println("In POST - First Servlet content");
        response.getWriter().println(request.getParameter("field"));
    }
    
}