package fr.pedro.event_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


@Component
public class UserClient {

    private static final Logger log = LoggerFactory.getLogger(UserClient.class);

    private final RestTemplate restTemplate;
    private final String userServiceBaseUrl;

    public UserClient(@Value("${user-service.base-url:http://localhost:8081}") String userServiceBaseUrl) {
        this.userServiceBaseUrl = userServiceBaseUrl;
        // Timeout
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(2000);
        this.restTemplate = new RestTemplate(factory);
    }

    @CircuitBreaker(name = "userClientCB", fallbackMethod = "checkUserExistsFallback")
    public boolean checkUserExists(String userId) {
        String url = String.format("%s/api/users/%s", userServiceBaseUrl, userId);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    @SuppressWarnings("unused")
    private boolean checkUserExistsFallback(String userId, Throwable ex){
        log.warn("Circuit breaker activé pour userId={} : {}", userId, ex.getMessage());
        return false;
    }
}
