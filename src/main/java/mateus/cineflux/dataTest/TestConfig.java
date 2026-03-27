package mateus.cineflux.dataTest;

import mateus.cineflux.user.User;
import mateus.cineflux.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        User u1 = new User("Maria Brown", new BCryptPasswordEncoder().encode("123456"));
        User u2 = new User("Alex Green", new BCryptPasswordEncoder().encode("123456"));
        User u3 = new User("JoãoZinho", new BCryptPasswordEncoder().encode("123456"));
        User u4 = new User("MateusinhoDoGrau", new BCryptPasswordEncoder().encode("123456"));

        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4));
    }
}
