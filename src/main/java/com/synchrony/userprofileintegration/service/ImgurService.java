package com.synchrony.userprofileintegration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.json.JSONObject;
import java.util.Base64;

@Service
public class ImgurService {

    @Value("${imgur.client-id}")
    private String clientId;

    @Value("${imgur.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/upload";
    private static final String IMGUR_DELETE_URL = "https://api.imgur.com/3/image/";

    public String uploadImage(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(fileBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Client-ID " + clientId);

            String body = "image=" + base64Image;
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(IMGUR_UPLOAD_URL, entity, String.class);
            JSONObject jsonResponse = new JSONObject(response.getBody());
            if (jsonResponse.getBoolean("success")) {
                JSONObject data = jsonResponse.getJSONObject("data");
                return data.getString("link");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            return jsonResponse.getBoolean("success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}