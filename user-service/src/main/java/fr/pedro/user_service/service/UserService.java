package fr.pedro.user_service.service;

import fr.pedro.user_service.dto.UserDTO;
import fr.pedro.user_service.entity.User;
import fr.pedro.user_service.mapper.UserMapper;
import fr.pedro.user_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<UserDTO> findAll() {
        return repo.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO save(UserDTO dto) {
        User saved = repo.save(UserMapper.toEntity(dto));
        return UserMapper.toDTO(saved);
    }
}
