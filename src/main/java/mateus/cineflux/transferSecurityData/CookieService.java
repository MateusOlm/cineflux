package mateus.cineflux.transferSecurityData;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import mateus.cineflux.transferSecurityData.refreshToken.RefreshToken;
import mateus.cineflux.transferSecurityData.refreshToken.RefreshTokenService;
import mateus.cineflux.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieService {

    @Value("${cineflux.app.access-token.cookie-name}")
    private String accessCookie;

    @Value("${cineflux.app.token.cookie-name}")
    private String tokenCookie;

    private JwtService jwtService;
    private RefreshTokenService refreshTokenService;

    public CookieService(JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public ResponseCookie generateAccessCookie(User user) {
        String jwt = jwtService.generateToken(user);
        ResponseCookie cookie = ResponseCookie.from(accessCookie, jwt).path("/").maxAge(2 * 60 * 60).httpOnly(true).build();
        return cookie;
    }

    public ResponseCookie generateRefreshTokenCookie(String username) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);
        ResponseCookie cookie = ResponseCookie.from(tokenCookie, refreshToken.getToken()).path("/").maxAge(2 * 60 * 60).httpOnly(true).build();
        return cookie;
    }

    public ResponseCookie cleanAccessCookie() {
        ResponseCookie cookie = ResponseCookie.from(accessCookie, null).path("/").build();
        return cookie;
    }

    public ResponseCookie cleanTokenCookie() {
        refreshTokenService.deleteRefreshToken(ResponseCookie.from(tokenCookie).toString());
        ResponseCookie cookie = ResponseCookie.from(tokenCookie, null).path("/").build();
        return cookie;
    }

    public String getAccessFromCookie(HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, accessCookie);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public String getTokenFromCookie(HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, tokenCookie);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
}
