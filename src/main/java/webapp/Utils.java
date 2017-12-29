package webapp;

import javax.servlet.http.Cookie;

/**
 * @author Mark Said Camilleri
 * @version 29/12/17.
 */
public class Utils {
    
    /**
     * Searches the list of cookies and returns the requested one
     * @param cookies The list of cookies to search
     * @param cookieName The cookie to look for
     * @return The cookie requested, null if not found.
     */
    public static Cookie findCookie(Cookie[] cookies, String cookieName){
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(cookieName)) return cookie;
        }
        
        return null;
    }
}
