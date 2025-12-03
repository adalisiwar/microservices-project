package com.example.adminservice.service;

import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class UserServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    // REST Client Methods - Synchronous Communication

    public String getAllUsers() {
        try {
            String url = userServiceUrl + "/api/users";
            log.info("Calling user service: {}", url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("User service response status: {}", response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling user service", e);
            throw new RuntimeException("Failed to fetch users from user service", e);
        }
    }

    public String getUserById(Long id) {
        try {
            String url = userServiceUrl + "/api/users/" + id;
            log.info("Calling user service: {}", url);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("User service response status: {}", response.getStatusCode());
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling user service", e);
            throw new RuntimeException("Failed to fetch user from user service", e);
        }
    }

    public void deactivateUser(Long id, String reason) {
        try {
            String url = userServiceUrl + "/api/users/" + id + "/deactivate?reason=" + URLEncoder.encode(reason, StandardCharsets.UTF_8);
            log.info("Calling user service: {}", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            log.info("User service response status: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Error calling user service", e);
            throw new RuntimeException("Failed to deactivate user in user service", e);
        }
    }


}
