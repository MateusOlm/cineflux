package mateus.cineflux.securities;

import mateus.cineflux.securities.dto.LoginRequest;
import mateus.cineflux.securities.dto.RegisterRequest;
import mateus.cineflux.transferSecurityData.CookieService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private CookieService cookieService;

    public SecurityController(UserService userService, AuthenticationManager authenticationManager, CookieService cookieService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.cookieService = cookieService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(usernamePassword);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResponseCookie jwtCookie = cookieService.generateJwtCookie((User) authentication.getPrincipal());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).build();
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
}
