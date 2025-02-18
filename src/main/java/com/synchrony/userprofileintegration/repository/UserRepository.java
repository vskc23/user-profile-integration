package com.synchrony.userprofileintegration.repository;
import com.synchrony.userprofileintegration.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Find a user by username for authentication or profile retrieval
    Optional<User> findByUsername(String username);
}