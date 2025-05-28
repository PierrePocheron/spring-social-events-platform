package fr.pedro.event_service.service;

import fr.pedro.event_service.dto.EventDTO;
import fr.pedro.event_service.entity.Event;
import fr.pedro.event_service.mapper.EventMapper;
import fr.pedro.event_service.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public List<EventDTO> getAll() {
        return repo.findAll().stream()
                .map(EventMapper::toDTO)
                .collect(Collectors.toList());
    }

    public EventDTO save(EventDTO dto) {
        Event saved = repo.save(EventMapper.toEntity(dto));
        return EventMapper.toDTO(saved);
    }
}
