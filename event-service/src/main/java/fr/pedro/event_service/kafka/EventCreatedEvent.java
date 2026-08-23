package fr.pedro.event_service.kafka;

import java.time.LocalDate;

public record EventCreatedEvent(
  Long eventId,
  String title,
  String organizerId,
  LocalDate date
) {

}
