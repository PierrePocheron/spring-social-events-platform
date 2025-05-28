package fr.pedro.user_service.controller;

import fr.pedro.user_service.dto.UserDTO;
import fr.pedro.user_service.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return service.findAll();
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO dto) {
        return service.save(dto);
    }
}
