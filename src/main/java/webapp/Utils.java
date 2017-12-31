package webapp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class Utils {

    /**
     * Searches the list of cookies and returns the requested one
     *
     * @param cookies    The list of cookies to search
     * @param cookieName The cookie to look for
     * @return The cookie requested, null if not found or if cookies was null
     */
    public static Cookie findCookie(Cookie[] cookies, String cookieName) {

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) return cookie;
        }

        return null;
    }

    public static void deleteCookie(Cookie cookie, HttpServletResponse response) {
        cookie.setValue(null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static void deleteCookie(String cookieName, HttpServletResponse response) {
        deleteCookie(new Cookie(cookieName, null), response);
    }

    public static String getHrefButton(String href, String id, String text, boolean disabled) {
        return "<a href=\"" + href + "\"><button id=\"" + id + "\" " +
                (disabled ? "disabled" : " ") + "> " + text + " </button></a>";
    }

    public static String getSubmitButton(String id, String text) {
        return "<button id=\"" + id + "\" type=\"submit\"> " + text + " </button>";
    }

    public static String getInputField(String id, String name, String placeholder, boolean required) {
        return "<input id=\"" + id + "\" name=\"" + name +
                "\" type=\"text\" placeholder=\"" + placeholder +
                "\" " + (required ? "required " : " ") + "/>";
    }

    public static String getPostForm(String id, String action) {
        return "<form id=\"" + id + "\" method=\"POST\" action=\"" + action + "\"/>";
    }

    public static String getTextArea(String id, String name, String placeholder, int rows, int cols) {
        return "<textarea id=\"" + id + "\" name=\"" + name +
                "\" placeholder=\"" + placeholder + "\" rows=\"" +
                rows + "\" cols=\"" + cols + "\"></textarea>";
    }
}
