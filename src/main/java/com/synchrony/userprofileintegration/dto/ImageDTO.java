package com.synchrony.userprofileintegration.dto;

import java.util.List;

/**
 * DTO for returning image details.
 */
public class ImageDTO {
    private Long id;
    private String imgurId;
    private String link;

    public ImageDTO(Long id, String imgurId, String link) {
        this.id = id;
        this.imgurId = imgurId;
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public String getImgurId() {
        return imgurId;
    }

    public String getLink() {
        return link;
    }
}