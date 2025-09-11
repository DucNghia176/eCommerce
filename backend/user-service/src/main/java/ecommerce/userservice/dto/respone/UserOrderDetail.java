package ecommerce.userservice.dto.respone;

import ecommerce.aipcommon.model.response.UserOrderDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserOrderDetail {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private int isLock;
    private List<UserOrderDetailResponse> userOrderDetailResponse;
}
