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
}
