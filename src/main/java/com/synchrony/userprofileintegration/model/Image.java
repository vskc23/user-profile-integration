package com.synchrony.userprofileintegration.model;

import jakarta.persistence.*;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imgur_link")
    private String link;

    @Column(name = "delete_hash", unique = true)
    private String deleteHash;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

