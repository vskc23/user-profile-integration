package com.synchrony.userprofileintegration.service;

import com.synchrony.userprofileintegration.dto.ImageResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class ImgurService {

    private static final Logger logger = LogManager.getLogger(ImgurService.class);


    @Value("${imgur.client-id}")
    private String clientId;

    @Value("${imgur.upload-url}")
    private String IMGUR_UPLOAD_URL;

    @Value("${imgur.delete-url}")
    private String IMGUR_DELETE_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    public ImageResponseDTO uploadImage(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            String base64Image = java.util.Base64.getEncoder().encodeToString(fileBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Client-ID " + clientId);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("image", base64Image);
            map.add("type", "base64");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(IMGUR_UPLOAD_URL, entity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            if (jsonResponse.getBoolean("success")) {
                JSONObject data = jsonResponse.getJSONObject("data");
                logger.info("Imgur upload successful. Link: {}", data.getString("link"));
                return new ImageResponseDTO(data.getString("link"), data.getString("deletehash"));
            }
            else{
                logger.error("Imgur upload response indicated failure: {}", jsonResponse);
            }
        } catch (Exception e) {
            logger.error("Exception occurred during Imgur upload", e);
        }
        return null;
    }

    public boolean deleteImage(String deleteHash) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Client-ID " + clientId);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(IMGUR_DELETE_URL + deleteHash, HttpMethod.DELETE, entity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            boolean success = jsonResponse.getBoolean("success");
            logger.info("Imgur delete response success: {}", success);
            return jsonResponse.getBoolean("success");
        } catch (Exception e) {
            logger.error("Exception occurred during Imgur deletion", e);
        }
        return false;
    }
}