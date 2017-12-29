package webapp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    public LoginServlet() {
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        response.getWriter().println("" +
                "<form method=\"POST\" action=\"loggedin\">" +
                "   <input name=\"id\" type=\"text\" placeholder=\"Agent ID\" /><br>" +
                "   <input name=\"name\" type=\"text\" placeholder=\"Agent name\" /><br>" +
                "   <input type=\"submit\" value=\"Login\" />" +
                "</form>"
        );
    }
}