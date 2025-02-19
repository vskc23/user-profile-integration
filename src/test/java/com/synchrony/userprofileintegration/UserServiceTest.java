package com.synchrony.userprofileintegration;


import com.synchrony.userprofileintegration.model.User;
import com.synchrony.userprofileintegration.repository.UserRepository;
import com.synchrony.userprofileintegration.service.UserService;
import exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldEncodePasswordAndSave() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("rawPassword");

        given(passwordEncoder.encode("rawPassword")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User result = userService.registerUser(user);

        assertNotNull(result.getId());
        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findByUsername_ShouldReturnUserIfFound() {
        User user = new User();
        user.setId(1L);
        user.setUsername("bob");

        given(userRepository.findByUsername("bob")).willReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername("bob");

        assertTrue(result.isPresent());
        assertEquals("bob", result.get().getUsername());
        verify(userRepository).findByUsername("bob");
    }

    @Test
    void findByUsername_ShouldThrowExceptionIfNotFound() {
        given(userRepository.findByUsername("charlie")).willReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findByUsername("charlie"));
    }

}
