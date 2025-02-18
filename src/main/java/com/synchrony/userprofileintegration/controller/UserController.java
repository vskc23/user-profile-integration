package com.synchrony.userprofileintegration.controller;

import com.synchrony.userprofileintegration.dto.ImageDTO;
import com.synchrony.userprofileintegration.dto.UserProfileDTO;
import com.synchrony.userprofileintegration.dto.UserRequestDTO;
import com.synchrony.userprofileintegration.dto.UserResponseDTO;
import com.synchrony.userprofileintegration.model.Image;
import com.synchrony.userprofileintegration.model.User;
import com.synchrony.userprofileintegration.service.ImgurService;
import com.synchrony.userprofileintegration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(userRequest.getPassword());

        User registeredUser = userService.registerUser(user);
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
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Convert List<Image> to List<ImageDTO>
            List<ImageDTO> imageDTOs = user.getImages().stream()
                    .map(image -> new ImageDTO(image.getId(), image.getImgurId(), image.getLink()))
                    .toList();

            // Create UserProfileDTO response
            UserProfileDTO userProfileDTO = new UserProfileDTO(user.getId(), user.getUsername(), imageDTOs);
            return ResponseEntity.ok(userProfileDTO);
        }
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
    @PostMapping("/users/{username}/images")
    public ResponseEntity<?> uploadImage(@PathVariable String username,
                                         @RequestParam("file") MultipartFile file) {
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String imageUrl = imgurService.uploadImage(file);

            if (imageUrl == null) {
                return ResponseEntity.status(500).body("Image upload failed");
            }

            // Create Image entity and add to user profile
            Image newImage = new Image();
            newImage.setImgurId("placeholder-delete-hash");
            newImage.setLink(imageUrl);
            user.getImages().add(newImage);
            userService.registerUser(user);

            // Convert to ImageDTO for response
            ImageDTO imageDTO = new ImageDTO(newImage.getId(), newImage.getImgurId(), newImage.getLink());
            return ResponseEntity.ok(imageDTO);
        }
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
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
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
        return ResponseEntity.notFound().build();
    }

}