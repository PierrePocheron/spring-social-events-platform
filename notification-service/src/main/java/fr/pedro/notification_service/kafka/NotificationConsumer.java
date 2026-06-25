package fr.pedro.notification_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

  private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

  @KafkaListener(topics = "events.created", groupId = "notification-group")
  public void onEventCreated(EventCreatedEvent event) {
    log.info("Nouvelle notification : événement '{}' créé par l'organisateur {}", event.getTitle(), event.getOrganizerId());
  }
}
