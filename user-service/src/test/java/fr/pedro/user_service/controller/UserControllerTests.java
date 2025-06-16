package fr.pedro.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.pedro.user_service.dto.UserDTO;
import fr.pedro.user_service.service.UserService;
import fr.pedro.user_service.constant.ErrorMessages;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void shouldNotCreateUser_WhenFirstnameIsBlank() throws Exception {
        UserDTO dto = UserDTO.builder()
                .firstname("")
                .lastname("Doe")
                .email("john.doe@example.com")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstname").value(ErrorMessages.FIRSTNAME_REQUIRED));
    }

    @Test
    void shouldNotCreateUser_WhenEmailIsInvalid() throws Exception {
        UserDTO dto = UserDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .email("not-an-email")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value(ErrorMessages.EMAIL_INVALID));
    }

    @Test
    void shouldReturnNotFound_WhenGettingUnknownUser() throws Exception {
        mockMvc.perform(get("/api/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFound_WhenDeletingUnknownUser() throws Exception {
        mockMvc.perform(delete("/api/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        UserDTO dto = UserDTO.builder()
                .firstname("Pedro")
                .lastname("Pocheron")
                .email("pedro@ex.com")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("Pedro"))              
                .andExpect(jsonPath("$.lastname").value("Pocheron"))
                .andExpect(jsonPath("$.email").value("pedro@ex.com"));
    }

}
