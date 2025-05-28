package fr.pedro.user_service.service;

import fr.pedro.user_service.dto.UserDTO;
import fr.pedro.user_service.entity.User;
import fr.pedro.user_service.mapper.UserMapper;
import fr.pedro.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnUserDTOList() {
        // Given
        User user1 = User.builder().id(1L).firstname("Pedro").lastname("Pocheron").email("p@ex.com").build();
        User user2 = User.builder().id(2L).firstname("Elisa").lastname("Orange").email("e@ex.com").build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserDTO> result = userService.findAll();

        // Then
        assertEquals(2, result.size());
        assertEquals("Pedro", result.get(0).getFirstname());
        assertEquals("Elisa", result.get(1).getFirstname());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void save_shouldPersistAndReturnDTO() {
        // Given
        UserDTO input = UserDTO.builder()
                .firstname("Pedro")
                .lastname("Pocheron")
                .email("p@ex.com")
                .build();

        User saved = User.builder()
                .id(99L)
                .firstname("Pedro")
                .lastname("Pocheron")
                .email("p@ex.com")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(saved);

        // When
        UserDTO result = userService.save(input);

        // Then
        assertNotNull(result.getId());
        assertEquals("Pedro", result.getFirstname());
        assertEquals("Pocheron", result.getLastname());
        assertEquals("p@ex.com", result.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }
}
