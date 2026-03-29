package mateus.cineflux.transferSecurityData.refreshToken;

import jakarta.transaction.Transactional;
import mateus.cineflux.user.User;
import mateus.cineflux.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${token.expiration}")
    private Integer tokenExpiration;
    private RefreshTokenRepository refreshTokenRepository;
    private UserService userService;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID().toString(),
                LocalDateTime.now().plusHours(tokenExpiration).toInstant(ZoneOffset.of("-03:00")),
                (User) userService.findByUserName(username));

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean veryExpirationToken(RefreshToken refreshToken) {
        if (refreshToken.getExpiratyDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            return false;
        }
        return true;
    }

    @Transactional
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
