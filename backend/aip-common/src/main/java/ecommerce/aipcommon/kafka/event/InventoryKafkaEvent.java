package ecommerce.aipcommon.kafka.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryKafkaEvent {
    String skuCode;
    int quantity;
}