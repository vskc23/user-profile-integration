package com.synchrony.userprofileintegration.service;
import com.synchrony.userprofileintegration.model.User;
import com.synchrony.userprofileintegration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Service class responsible for handling user registration, updates, and retrieval.
 * It ensures that user passwords are securely encoded before persistence.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user by encoding the password and saving the user data.
     * If a user already exists, the same method can be used to update the profile.
     *
     * @param user the user details to be registered or updated.
     * @return the persisted user entity.
     */
    public User registerUser(User user) {
        // Securely encode the user's password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their unique username.
     *
     * @param username the unique username of the user.
     * @return an Optional containing the user if found, or empty if not.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}