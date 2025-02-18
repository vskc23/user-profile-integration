package com.synchrony.userprofileintegration.dto;

import java.util.List;

/**
 * DTO for returning a user's profile, including associated images.
 */
public class UserProfileDTO {
    private Long id;
    private String username;
    private List<ImageDTO> images;

    public UserProfileDTO(Long id, String username, List<ImageDTO> images) {
        this.id = id;
        this.username = username;
        this.images = images;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public List<ImageDTO> getImages() {
        return images;
    }
}