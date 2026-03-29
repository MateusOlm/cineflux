package mateus.cineflux.securities;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import mateus.cineflux.securities.dto.LoginRequest;
import mateus.cineflux.securities.dto.RegisterRequest;
import mateus.cineflux.transferSecurityData.CookieService;
import mateus.cineflux.transferSecurityData.refreshToken.RefreshToken;
import mateus.cineflux.transferSecurityData.refreshToken.RefreshTokenService;
import mateus.cineflux.user.User;
import mateus.cineflux.user.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private CookieService cookieService;
    private RefreshTokenService refreshTokenService;

    public SecurityController(UserService userService,
                              AuthenticationManager authenticationManager,
                              CookieService cookieService,
                              RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.cookieService = cookieService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest servletRequest) {

        String token = cookieService.getTokenFromCookie(servletRequest);
        if (token != null) {
            refreshTokenService.deleteRefreshToken(token);
        }

        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(usernamePassword);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        ResponseCookie accessCookie = cookieService.generateAccessCookie(user);
        ResponseCookie tokenCookie = cookieService.generateRefreshTokenCookie(user.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if(this.userService.findByUserName(registerRequest.username()) != null) {
            return ResponseEntity.badRequest().body("Já existe alguém com esse username");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequest.password());
        User user = new User(registerRequest.username(), encryptedPassword);
        userService.insert(user);

        return ResponseEntity.ok().build();
    }

        @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest servletRequest) {

        RefreshToken refreshToken = refreshTokenService.findByToken(cookieService.getTokenFromCookie(servletRequest));
        if (refreshToken == null) {
            ResponseCookie accessCookie = cookieService.cleanAccessCookie();
            ResponseCookie tokenCookie = cookieService.cleanTokenCookie();
            return ResponseEntity
                    .badRequest()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                    .body("Faça um novo login na aplicação");
        }
        boolean expirationToken = refreshTokenService.veryExpirationToken(refreshToken);
        System.out.println(expirationToken);
        if (!expirationToken) {
            ResponseCookie accessCookie = cookieService.cleanAccessCookie();
            ResponseCookie tokenCookie = cookieService.cleanTokenCookie();
            return ResponseEntity
                    .badRequest()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                    .body("Faça um novo login na aplicação");
        }
        ResponseCookie accessCookie = cookieService.generateAccessCookie(refreshToken.getUser());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .build();
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest servletRequest) {

        String token = cookieService.getTokenFromCookie(servletRequest);
        refreshTokenService.deleteRefreshToken(token);

        ResponseCookie accessCookie = cookieService.cleanAccessCookie();
        ResponseCookie tokenCookie = cookieService.cleanTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .build();

    }
}
