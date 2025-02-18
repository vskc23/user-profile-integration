package com.synchrony.userprofileintegration.controller;

import com.synchrony.userprofileintegration.model.Image;
import com.synchrony.userprofileintegration.model.User;
import com.synchrony.userprofileintegration.service.ImgurService;
import com.synchrony.userprofileintegration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;

/**
 * REST controller for managing user profiles and image operations.
 * Provides endpoints for user registration, profile retrieval, image upload, and image deletion.
 */
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final ImgurService imgurService;

    @Autowired
    public UserController(UserService userService, ImgurService imgurService) {
        this.userService = userService;
        this.imgurService = imgurService;
    }

    /**
     * Registers a new user using the provided registration details.
     *
     * @param user the user object containing registration information.
     * @return the registered user details along with an HTTP OK status.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    /**
     * Retrieves the profile of a user identified by the provided username.
     * The profile includes basic user details and the list of associated images.
     *
     * @param username the username of the user.
     * @return the user profile if found; otherwise, an HTTP 404 status.
     */
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        Optional<User> userOpt = userService.findByUsername(username);
        return userOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Uploads an image file for a specified user.
     * The image is uploaded to Imgur and its details are associated with the user's profile.
     *
     * @param username the username of the user.
     * @param file the image file to be uploaded.
     * @return the details of the uploaded image or an error message if the upload fails.
     */
    @PostMapping("/users/{username}/images")
    public ResponseEntity<?> uploadImage(@PathVariable String username,
                                         @RequestParam("file") MultipartFile file) {
        Optional<User> userOpt = userService.findByUsername(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        String imageUrl = imgurService.uploadImage(file);
        if (imageUrl == null) {
            return ResponseEntity.status(500).body("Image upload failed");
        }
        // Create an Image entity with the details received from Imgur
        Image newImage = new Image();
        newImage.setImgurId("placeholder-delete-hash"); // Replace with the actual delete hash when available
        newImage.setLink(imageUrl);

        User user = userOpt.get();
        user.getImages().add(newImage);
        userService.registerUser(user);
        return ResponseEntity.ok(newImage);
    }

    /**
     * Deletes an image from the user's profile.
     * The image is first removed from Imgur, and upon successful deletion, it is removed from the user's profile.
     *
     * @param username the username of the user.
     * @param imageId the unique identifier of the image to be deleted.
     * @return an HTTP response indicating the outcome of the deletion operation.
     */
    @DeleteMapping("/users/{username}/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable String username,
                                         @PathVariable Long imageId) {
        Optional<User> userOpt = userService.findByUsername(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Image targetImage = user.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElse(null);
        if (targetImage == null) {
            return ResponseEntity.notFound().build();
        }
        boolean deletionSuccess = imgurService.deleteImage(targetImage.getImgurId());
        if (!deletionSuccess) {
            return ResponseEntity.status(500).body("Image deletion failed");
        }
        user.getImages().remove(targetImage);
        userService.registerUser(user);
        return ResponseEntity.ok("Image deleted successfully");
    }
}