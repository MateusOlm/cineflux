package mateus.cineflux.securities;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mateus.cineflux.transferSecurityData.CookieService;
import mateus.cineflux.transferSecurityData.JwtService;
import mateus.cineflux.user.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class VeryPerRequest extends OncePerRequestFilter {

    private JwtService jwtService;
    private CookieService cookieService;
    private UserRepository userRepository;

    public VeryPerRequest(JwtService jwtService, UserRepository userRepository, CookieService cookieService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.cookieService = cookieService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = parseToken(request);
        if(token != null) {
            String username = jwtService.validateToken(token);
            if (username != null) {
                UserDetails user = userRepository.findByUsername(username);
                UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePassword);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request) {
        return cookieService.getAccessFromCookie(request);
    }
}
