package com.synchrony.userprofileintegration.controller;

import com.synchrony.userprofileintegration.dto.*;
import com.synchrony.userprofileintegration.model.Image;
import com.synchrony.userprofileintegration.model.User;
import com.synchrony.userprofileintegration.service.ImgurService;
import com.synchrony.userprofileintegration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * REST controller for managing user profiles and image operations.
 * Provides endpoints for user registration, profile retrieval, image upload, and image deletion.
 */
@RestController
@RequestMapping("/api")
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
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
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(userRequest.getPassword());
        logger.info("Registering user: {}", user.getUsername());
        User registeredUser = userService.registerUser(user);
        logger.info("User registered with id: {}", registeredUser.getId());
        UserResponseDTO responseDTO = new UserResponseDTO(registeredUser.getId(), registeredUser.getUsername());
        return ResponseEntity.ok(responseDTO);
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
        logger.info("Fetching profile for user: {}", username);
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("User found: {}", user.getUsername());
            List<ImageDTO> imageDTOs = user.getImages().stream()
                    .map(image -> new ImageDTO(image.getId(), image.getDeleteHash(), image.getLink()))
                    .toList();


            UserProfileDTO userProfileDTO = new UserProfileDTO(user.getId(), user.getUsername(), imageDTOs);
            return ResponseEntity.ok(userProfileDTO);
        }
        logger.warn("User not found: {}", username);
        return ResponseEntity.notFound().build();
    }




    /**
     * Uploads an image file for a specified user.
     * The image is uploaded to Imgur and its details are associated with the user's profile.
     *
     * @param username the username of the user.
     * @param file the image file to be uploaded.
     * @return the details of the uploaded image or an error message if the upload fails.
     */
    @PostMapping(value = "/users/{username}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@PathVariable String username,
                                         @RequestParam("file") MultipartFile file) {
        logger.info("Uploading image for user: {}", username);
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            ImageResponseDTO imgurResponse = imgurService.uploadImage(file);
            if (imgurResponse == null || imgurResponse.getLink() == null) {
                logger.error("Image upload failed for user: {}", username);
                return ResponseEntity.status(500).body("Image upload failed");
            }
            Image newImage = new Image();
            newImage.setLink(imgurResponse.getLink());
            newImage.setDeleteHash(imgurResponse.getDeleteHash());
            user.getImages().add(newImage);
            userService.updateUser(user);
            logger.info("Image uploaded successfully for user: {}", username);
            return ResponseEntity.ok(newImage);
        }
        logger.warn("User not found during image upload: {}", username);
        return ResponseEntity.notFound().build();
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
        logger.info("Deleting image id {} for user: {}", imageId, username);
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Image targetImage = user.getImages().stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElse(null);
            if (targetImage == null) {
                logger.warn("Image id {} not found for user: {}", imageId, username);
                return ResponseEntity.notFound().build();
            }
            boolean deletionSuccess = imgurService.deleteImage(targetImage.getDeleteHash());

            if (!deletionSuccess) {
                logger.error("Failed to delete image id {} for user: {}", imageId, username);
                return ResponseEntity.status(500).body("Image deletion failed");
            }
            user.getImages().remove(targetImage);
            userService.updateUser(user);
            logger.info("Image id {} deleted successfully for user: {}", imageId, username);
            return ResponseEntity.ok("Image deleted successfully");
        }
        logger.warn("User not found during image deletion: {}", username);
        return ResponseEntity.notFound().build();
    }




}