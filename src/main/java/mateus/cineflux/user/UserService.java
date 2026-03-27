package mateus.cineflux.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public UserDetails findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    public User insert(User user) {
        return userRepository.save(user);
    }
}
