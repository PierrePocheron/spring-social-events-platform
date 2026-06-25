package fr.pedro.notification_service.kafka;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreatedEvent {
    private Long eventId;
    private String title;
    private String organizerId;
    private LocalDate date;
}