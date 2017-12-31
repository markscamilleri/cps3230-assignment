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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final MessagingSystem messagingSystem;

    public RegisterServlet(final MessagingSystem messagingSystem) {
        this.messagingSystem = messagingSystem;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        response.getWriter().println("" +
                "<h1>Register Screen</h1>" +
                Utils.getPostForm("registerForm", "/register") +
                Utils.getInputField("id", "id", "Agent ID", true) + "<br>" +
                Utils.getInputField("name", "idname", "Agent name", true) + "<br>" +
                Utils.getSubmitButton("submit", "Register")
        );
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final String id = request.getParameter("id");
        final String name = request.getParameter("name");

        final Supervisor supervisor = new SupervisorImpl(messagingSystem);
        final Agent agent = new Agent(id, supervisor, messagingSystem);
        if (agent.register()) {
            response.addCookie(new Cookie(CookieNames.AGENT_ID.name(), id));
            response.addCookie(new Cookie(CookieNames.LOGIN_KEY.name(), agent.getLoginKey()));
            response.sendRedirect("/login");
        } else {
            response.sendRedirect("/register");
        }
    }
}