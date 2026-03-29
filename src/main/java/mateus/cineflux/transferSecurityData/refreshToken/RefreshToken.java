package mateus.cineflux.transferSecurityData.refreshToken;

import jakarta.persistence.*;
import mateus.cineflux.user.User;

import java.time.Instant;

@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;

    private Instant expiratyDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    public RefreshToken(String token, Instant expiratyDate, User user) {
        this.token = token;
        this.expiratyDate = expiratyDate;
        this.user = user;
    }

    public RefreshToken() {}

    public String getToken() {
        return token;
    }

    public Instant getExpiratyDate() {
        return expiratyDate;
    }

    public User getUser() {
        return user;
    }

}
