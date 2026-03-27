package mateus.cineflux.transferSecurityData;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import mateus.cineflux.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieService {

    private JwtService jwtService;

    @Value("${cineflux.app.jwtCookieName}")
    private String jwtCookie;

    public CookieService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public ResponseCookie generateJwtCookie(User user) {
        String jwt = jwtService.generateToken(user);
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/").maxAge(2 * 60 * 60).httpOnly(true).build();
        return cookie;
    }

    public ResponseCookie cleanCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/auth").build();
        return cookie;
    }

    public String getJwtFromCookie(HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, jwtCookie);
        if (cookie != null) {
            System.out.println(cookie.getValue());
            return cookie.getValue();
        }
        return null;
    }
}
