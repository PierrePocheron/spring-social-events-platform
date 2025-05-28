package fr.pedro.event_service.dto;

import fr.pedro.event_service.entity.EventType;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Long id;
    private String title;
    private String location;
    private LocalDate date;
    private EventType type;
    private String organizerId;
}
