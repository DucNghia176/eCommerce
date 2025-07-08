package ecommerce.userservice.dto.request;

import lombok.*;

import java.time.LocalDate;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String fullName;
    private String avatar;
    private String gender;
    private LocalDate dateOfBirth;
}
