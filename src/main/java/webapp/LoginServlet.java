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

        final Cookie idCookie = Utils.findCookie(request.getCookies(), CookieNames.AGENT_ID.name());
        final Cookie lKeyCookie = Utils.findCookie(request.getCookies(), CookieNames.LOGIN_KEY.name());

        if (idCookie == null || lKeyCookie == null) {
            response.sendRedirect("/register");
        } else {
            response.setContentType("text/html");
            response.getWriter().println("" +
                    "<h1>Login Screen</h1>" +
                    "<hr>" +
                    Utils.getPostForm("loginForm", "/login") +
                    "<p>" +
                    "    <b>Agent ID</b>:  " + Utils.getSpan("id", idCookie.getValue()) + "<br>" +
                    "    <b>Login key</b>: " + Utils.getSpan("lKey", lKeyCookie.getValue()) + "<br>" +
                    "</p>" +
                    Utils.getInputField("lKeyInput", "Confirm login key", true) + "<br>" +
                    Utils.getSubmitButton("submit", "Login")
            );
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        final Cookie idCookie = Utils.findCookie(request.getCookies(), CookieNames.AGENT_ID.name());
        final Cookie lKeyCookie = Utils.findCookie(request.getCookies(), CookieNames.LOGIN_KEY.name());
        final String lKey = request.getParameter("lKeyInput");

        if (idCookie == null || lKeyCookie == null) {
            response.sendRedirect("/register");
        } else {
            final Supervisor supervisor = new SupervisorImpl(messagingSystem);
            final Agent agent = new Agent(idCookie.getValue(), supervisor, messagingSystem, lKey);

            if (agent.login()) {
                Utils.deleteCookie(lKeyCookie, response);
                response.addCookie(new Cookie(CookieNames.SESSION_KEY.name(), agent.getSessionKey()));
                response.sendRedirect("/sendmail");
            } else {
                response.sendRedirect("/login");
            }
        }
    }
}