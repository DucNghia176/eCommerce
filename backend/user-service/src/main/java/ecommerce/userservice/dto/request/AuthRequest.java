package ecommerce.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String username;
    @Email(message = "Email phải đúng định dạng")
    private String email;
    @NotBlank(message = "Password không được trống")
    private String password;
}
