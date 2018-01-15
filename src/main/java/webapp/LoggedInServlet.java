package webapp;

import system.MessagingSystem;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/loggedin")
public class LoggedInServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");

        final Cookie idCookie = Utils.findCookie(request.getCookies(), CookieNames.AGENT_ID.name());
        final Cookie skCookie = Utils.findCookie(request.getCookies(), CookieNames.SESSION_KEY.name());

        if (idCookie == null || skCookie == null) {
            response.sendRedirect("/register");
        } else {
            final String id = idCookie.getValue();

            response.getWriter().println("" +
                    "<h1>Agent " + id + "'s Mailbox</h1>" +
                    "<hr>" +
                    Utils.getHrefButton("/readmessage", "consumeMessage", "Get Next Message") + "<br>" +
                    Utils.getHrefButton("/sendmessage", "sendMessage", "Send a Message") + "<br>" +
                    "<hr>" +
                    Utils.getHrefButton("/logout", "logout", "Logout")
            );
        }
    }
}