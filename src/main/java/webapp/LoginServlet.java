package webapp;

import system.Agent;
import system.MessagingSystem;
import system.Supervisor;
import system.SupervisorImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final MessagingSystem messagingSystem;

    public LoginServlet(final MessagingSystem messagingSystem) {
        this.messagingSystem = messagingSystem;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        response.getWriter().println("" +
                "<h1>Login Screen</h1>" +
                "<form method=\"POST\" action=\"login\">" +
                "   <input id=\"id\" name=\"id\" type=\"text\" placeholder=\"Agent ID\" /><br>" +
                "   <input id==\"name\" name=\"name\" type=\"text\" placeholder=\"Agent name\" /><br>" +
                "   <input id=\"submit\" type=\"submit\" value=\"Login\" />" +
                "</form>"
        );
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String id = request.getParameter("id");
        final String name = request.getParameter("name");
        final Supervisor supervisor = new SupervisorImpl();

        final Agent agent = new Agent(id, name, supervisor, messagingSystem);
        if (agent.login()) {
            response.addCookie(new Cookie("id", id));
            response.addCookie(new Cookie("skey", agent.getSessionKey()));
            response.sendRedirect("/sendmail");
        } else {
            response.sendRedirect("/login");
        }
    }
}