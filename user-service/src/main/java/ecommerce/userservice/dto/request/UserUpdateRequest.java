package ecommerce.userservice.dto.request;

import ecommerce.aipcommon.model.status.GenderStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String fullName;
    private GenderStatus gender;
    private LocalDate dateOfBirth;
}
