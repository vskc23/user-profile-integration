package com.synchrony.userprofileintegration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security configuration for the application.
 * Defines beans required for securing the application, including the PasswordEncoder.
 */
@Configuration
public class SecurityConfig {

    /**
     * Creates a PasswordEncoder bean that uses BCrypt hashing.
     *
     * @return a PasswordEncoder instance for encoding user passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disabling CSRF for API usage
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/register", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())  // Enables default login form authentication
                .logout(withDefaults());    // Enables logout functionality
        return http.build();
    }

}