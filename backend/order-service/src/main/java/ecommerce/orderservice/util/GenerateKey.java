package ecommerce.orderservice.util;

import ecommerce.orderservice.dto.request.OrderItemRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenerateKey {
    public String generateCartSignature(List<OrderItemRequest> items) {
        String base = items.stream()
                .sorted(Comparator.comparing(OrderItemRequest::getProductId))
                .map(i -> i.getProductId() + ":" + i.getQuantity())
                .collect(Collectors.joining(","));
        // Làm tròn thời gian đến phút
        String minute = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).toString();
        return base + ":" + minute;
    }

    public String generateOrderCode() {
        String prefix = "ORD";
        String dateTimePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        int random = (int) (Math.random() * 900) + 100;
        return prefix + "-" + dateTimePart + "-" + random;
    }
}
