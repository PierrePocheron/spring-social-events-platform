package fr.pedro.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import fr.pedro.user_service.constant.ErrorMessages;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = ErrorMessages.FIRSTNAME_REQUIRED)
    private String firstname;

    @NotBlank(message = ErrorMessages.LASTNAME_REQUIRED)
    private String lastname;

    @NotBlank(message = ErrorMessages.EMAIL_REQUIRED)
    @Email(message = ErrorMessages.EMAIL_INVALID)
    private String email;
}
