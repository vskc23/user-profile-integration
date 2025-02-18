package com.synchrony.userprofileintegration.dto;


public class ImageResponseDTO {
    private String link;
    private String deleteHash;

    public ImageResponseDTO() {
    }

    public ImageResponseDTO(String link, String deleteHash) {
        this.link = link;
        this.deleteHash = deleteHash;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDeleteHash() {
        return deleteHash;
    }

    public void setDeleteHash(String deleteHash) {
        this.deleteHash = deleteHash;
    }
}
