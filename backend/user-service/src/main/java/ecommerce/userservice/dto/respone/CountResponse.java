package ecommerce.userservice.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class CountResponse {
    private Long all;
    private Long active;
    private Long inactive;
}