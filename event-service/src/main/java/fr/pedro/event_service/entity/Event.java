package fr.pedro.event_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String location;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private EventType type;

    private String organizerId; // ID utilisateur, à relier plus tard
}
