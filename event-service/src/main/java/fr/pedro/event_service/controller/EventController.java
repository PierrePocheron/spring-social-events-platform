package fr.pedro.event_service.controller;

import fr.pedro.event_service.dto.EventDTO;
import fr.pedro.event_service.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventDTO> all() {
        return service.getAll();
    }

    @PostMapping
    public EventDTO create(@RequestBody EventDTO dto) {
        return service.save(dto);
    }
}
