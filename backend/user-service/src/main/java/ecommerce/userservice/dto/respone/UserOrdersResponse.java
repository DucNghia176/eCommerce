package ecommerce.userservice.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrdersResponse {
    private Long id;
    private String name;
    private String avatar;
    private String address;
    private Long totalOrders;
    private BigDecimal totalAmount;

    private int isLock;

    public UserOrdersResponse(Long id, String name, String avatar, String address, int isLock) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.address = address;
        this.isLock = isLock;
    }
}
