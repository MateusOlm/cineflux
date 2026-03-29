package mateus.cineflux.transferSecurityData;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import mateus.cineflux.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class JwtService {

    @Value("${app.private-key}")
    private RSAPrivateKey privateKey;

    @Value("${app.public-key}")
    private RSAPublicKey publicKey;

    @Value("${access.expiration}")
    private Integer accessExpiration;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);

            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getUsername())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar token", e);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);

            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusMinutes(accessExpiration).toInstant(ZoneOffset.of("-03:00"));
    }
}
