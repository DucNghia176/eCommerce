package ecommerce.apicommon1.kafka.event;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateEvent {
    private String skuCode;
    private String name;
    //    private Integer quantity;
    private BigDecimal importPrice;
}
