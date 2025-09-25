package ecommerce.orderservice.util;

import ecommerce.orderservice.dto.request.OrderItemRequest;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenerateKey {
    public String generateCartSignature(List<OrderItemRequest> items) {
        return items.stream()
                .sorted(Comparator.comparing(OrderItemRequest::getProductId))
                .map(i -> i.getProductId() + ":" + i.getQuantity())
                .collect(Collectors.joining(","));
    }
}
