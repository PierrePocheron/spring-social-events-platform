package fr.pedro.event_service.mapper;

import fr.pedro.event_service.dto.EventDTO;
import fr.pedro.event_service.entity.Event;

public class EventMapper {

    public static EventDTO toDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .location(event.getLocation())
                .date(event.getDate())
                .type(event.getType())
                .organizerId(event.getOrganizerId())
                .build();
    }

    public static Event toEntity(EventDTO dto) {
        return Event.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .location(dto.getLocation())
                .date(dto.getDate())
                .type(dto.getType())
                .organizerId(dto.getOrganizerId())
                .build();
    }
}
