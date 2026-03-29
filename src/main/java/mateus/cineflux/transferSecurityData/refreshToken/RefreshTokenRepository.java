package mateus.cineflux.transferSecurityData.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    void deleteByToken(String token);
    RefreshToken findByToken(String token);
}
