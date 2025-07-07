package fr.pedro.event_service.init;

import fr.pedro.event_service.dto.EventDTO;
import fr.pedro.event_service.entity.EventType;
import fr.pedro.event_service.repository.EventRepository;
import fr.pedro.event_service.service.EventService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DevDataLoader {

    private final EventService eventService;
    private final EventRepository eventRepository;

    @Value("${app.init.dev-dataset:false}")
    private boolean devDatasetEnabled;

    @PostConstruct
    public void initEvents() {
      System.out.println("🧪 DevDataLoader lancé"); // test temporaire
        if (!devDatasetEnabled || eventRepository.count() > 0) return;

        System.out.println("📆 Attente que le user-service soit prêt...");
        waitForUserService();

        System.out.println("📆 Insertion des événements de développement...");

        try {
            eventService.save(EventDTO.builder()
                    .title("Apéro Chill")
                    .location("Chez Pierre")
                    .date(LocalDate.of(2025, 6, 20))
                    .type(EventType.CONCERT)
                    .organizerId("1")
                    .build());

            eventService.save(EventDTO.builder()
                    .title("Voyage Crète")
                    .location("La Canée")
                    .date(LocalDate.of(2025, 6, 22))
                    .type(EventType.SPORT)
                    .organizerId("2")
                    .build());

            System.out.println("✅ Événements insérés !");
        } catch (Exception e) {
            System.err.println("❌ Erreur d'insertion d'événements : " + e.getMessage());
        }
    }

    private void waitForUserService() {
        RestTemplate restTemplate = new RestTemplate();
        int maxRetries = 10;
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                restTemplate.getForObject("http://user-service:8081/api/users", String.class);
                System.out.println("✅ user-service est prêt !");
                return;
            } catch (Exception e) {
                attempt++;
                System.out.println("⏳ Tentative " + attempt + " : user-service non disponible...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }
        System.err.println("⚠️ Impossible de contacter user-service après " + maxRetries + " tentatives.");
    }
}
