package ecommerce.aipcommon.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    Long userId;
    Long productId;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal discount;
    Integer isSelected;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
