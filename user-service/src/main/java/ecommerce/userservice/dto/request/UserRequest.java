package ecommerce.userservice.dto.request;

import ecommerce.aipcommon.model.status.GenderStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "Username không được trống")
    @Size(min = 8, message = "Username tối thiểu 8 ký tự")
    private String username;
    @NotBlank(message = "Password không được trống")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&~^\\/\\[\\]{}()<>.,:;|#\\-_+=`'\"\\\\]).{8,}$",
            message = "Password phải tối thiểu 8 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt"
    )
    @Size(min = 8, message = "Username tối thiểu 8 ký tự")
    private String password;
    private String fullName;
    @NotBlank(message = "Email không được trống")
    @Email(message = "Email phải đúng định dạng")
    private String email;
    private String role;
    private GenderStatus gender;
    private LocalDate dateOfBirth;
}
