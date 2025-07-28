package ecommerce.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String usernameOrEmail;
    @NotBlank(message = "Password không được trống")
    private String password;
}
