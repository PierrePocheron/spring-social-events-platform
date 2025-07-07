package fr.pedro.event_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;

    public UserClient(@Value("${user-service.base-url:http://localhost:8081}") String userServiceBaseUrl) {
        this.userServiceBaseUrl = userServiceBaseUrl;
        this.restTemplate = new RestTemplate();
    }

    public boolean checkUserExists(String userId) {
        try {
            String url = String.format("%s/api/users/%s", userServiceBaseUrl, userId);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
