package ecommerce.userservice.dto.request;

import ecommerce.aipcommon.model.status.GenderStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoUpdateRequest {
    private String fullName;
    private GenderStatus gender;
    private LocalDate dateOfBirth;
    private String address;
    private String phone;
}
