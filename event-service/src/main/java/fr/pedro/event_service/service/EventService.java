package fr.pedro.event_service.service;

import fr.pedro.event_service.dto.EventDTO;
import fr.pedro.event_service.entity.Event;
import fr.pedro.event_service.mapper.EventMapper;
import fr.pedro.event_service.repository.EventRepository;
import org.springframework.stereotype.Service;
import fr.pedro.event_service.client.UserClient;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repo;
    private final UserClient userClient;

    public List<EventDTO> getAll() {
        return repo.findAll().stream()
                .map(EventMapper::toDTO)
                .collect(Collectors.toList());
    }

    public EventDTO save(EventDTO dto) {
        if (!userClient.checkUserExists(dto.getOrganizerId())) {
            throw new IllegalArgumentException("Organizer ID does not exist in user-service.");
        }
        Event saved = repo.save(EventMapper.toEntity(dto));
        return EventMapper.toDTO(saved);
    }
}
