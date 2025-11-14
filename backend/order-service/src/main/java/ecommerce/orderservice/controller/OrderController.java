package ecommerce.orderservice.controller;

import ecommerce.apicommon1.model.request.UpdateOrderStatusRequest;
import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.response.UpdateOrderStatusResponse;
import ecommerce.apicommon1.model.response.UserOrderDetailResponse;
import ecommerce.orderservice.dto.request.OrderCreateRequest;
import ecommerce.orderservice.dto.response.OrderCreateResponse;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.dto.response.OrdersAD;
import ecommerce.orderservice.dto.response.PaymentOrderResponse;
import ecommerce.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/create")
    public ApiResponse<OrderCreateResponse> order(@Valid @RequestBody OrderCreateRequest request) {
        OrderCreateResponse response = orderService.order(request);
        return ApiResponse.<OrderCreateResponse>builder()
                .code(200)
                .message("Order created successfully")
                .data(response)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("")
    public ApiResponse<Page<OrdersAD>> getOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getOrders(page, size);
    }

    @GetMapping("/{orderId}")
    public PaymentOrderResponse getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/quantity")
    public Map<Long, Long> extractOrderQuantity(@RequestParam List<Long> usersId) {
        return orderService.extractOrderQuantity(usersId);
    }

    @GetMapping("/user/{userId}")
    public List<UserOrderDetailResponse> findOrdersDetailByUserId(@PathVariable("userId") Long userId) {
        return orderService.getUserOrderDetail(userId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/update-status")
    public ApiResponse<UpdateOrderStatusResponse> updateOrderStatus(@RequestBody UpdateOrderStatusRequest request) {
        UpdateOrderStatusResponse response = orderService.updateOrderStatus(request);
        return ApiResponse.<UpdateOrderStatusResponse>builder()
                .code(200)
                .message("Cập nhật trạng thái đơn hàng thành công")
                .data(response)
                .build();
    }

    @GetMapping("/exists")
    boolean hasPurchased(@RequestParam Long userId, @RequestParam Long productId) {
        return orderService.existsByUserIdAndProductIdAndStatus(userId, productId);
    }

    @GetMapping("/my-order")
    public ApiResponse<List<OrderResponse>> getOrderByUserId() {
        List<OrderResponse> response = orderService.getOrderByUserId();
        return ApiResponse.<List<OrderResponse>>builder()
                .code(200)
                .message("Lấy dữ liệu thành công với")
                .data(response)
                .build();
    }
}
