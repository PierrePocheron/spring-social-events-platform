package fr.pedro.event_service.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventProducer {

  private static final Logger log = LoggerFactory.getLogger(EventProducer.class);
  static final String TOPIC = "events.created";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void publishEventCreated(EventCreatedEvent event) {
    kafkaTemplate.send(TOPIC, event.eventId().toString(), event);
    log.info("Evenement publié sur {} : eventId={}", TOPIC, event.eventId());
  }

}
