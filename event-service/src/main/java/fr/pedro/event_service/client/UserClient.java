package fr.pedro.event_service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate;

    public UserClient() {
        this.restTemplate = new RestTemplate();
    }

    public boolean checkUserExists(String userId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/api/users/" + userId, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}
