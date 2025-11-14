package ecommerce.apicommon1.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingKafkaEvent {
    private Long orderId;
    private Long userId;
    private String orderCode;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<ProductItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        private Long productId;
        private String productName;
        private int quantity;
        private BigDecimal price;
    }
}
