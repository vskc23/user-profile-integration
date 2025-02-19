package com.synchrony.userprofileintegration.service;

import com.synchrony.userprofileintegration.model.User;
import com.synchrony.userprofileintegration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the domain User from the repository
        User domainUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert domain User into a Spring Security UserDetails object.
        return org.springframework.security.core.userdetails.User
                .withUsername(domainUser.getUsername())
                .password(domainUser.getPassword())
                .roles("USER") // Hardcoded role for now
                .build();
    }
}