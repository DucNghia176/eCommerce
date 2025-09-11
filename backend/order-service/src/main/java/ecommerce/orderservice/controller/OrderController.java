package ecommerce.orderservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserOrderDetailResponse;
import ecommerce.orderservice.dto.request.OrderRequest;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.dto.response.OrdersAD;
import ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @GetMapping
    public ApiResponse<Page<OrdersAD>> getOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getOrders(page, size);
    }

    @GetMapping("/quantity")
    public Map<Long, Long> extractOrderQuantity(@RequestParam List<Long> usersId) {
        return orderService.extractOrderQuantity(usersId);
    }

    @GetMapping("/{userId}")
    public List<UserOrderDetailResponse> findOrdersDetailByUserId(@PathVariable Long userId) {
        return orderService.getUserOrderDetail(userId);
    }
}
