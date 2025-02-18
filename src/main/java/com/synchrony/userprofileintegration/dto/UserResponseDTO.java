package com.synchrony.userprofileintegration.dto;

/**
 * DTO for user registration responses.
 */
public class UserResponseDTO {
    private Long id;
    private String username;

    public UserResponseDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}