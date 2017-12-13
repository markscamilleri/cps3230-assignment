package webapp;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HttpRequestHandler extends AbstractHandler {

    final String greeting;
    final String body;

    public HttpRequestHandler() {
        this("Hello World");
    }

    public HttpRequestHandler(String greeting) {
        this(greeting, null);
    }

    public HttpRequestHandler(String greeting, String body) {
        this.greeting = greeting;
        this.body = body;
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {

        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();

        out.println("<h1>" + greeting + "</h1>");
        if (body != null) {
            out.println(body);
        }

        baseRequest.setHandled(true);
    }
}