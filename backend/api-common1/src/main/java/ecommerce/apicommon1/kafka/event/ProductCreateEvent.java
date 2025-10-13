package ecommerce.apicommon1.kafka.event;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCreateEvent {
    private Long productId;
    private String skuCode;
    private String name;
    //    private Integer quantity;
    private BigDecimal importPrice;
}
