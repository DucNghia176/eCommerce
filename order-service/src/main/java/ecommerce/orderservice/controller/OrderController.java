package ecommerce.orderservice.controller;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.orderservice.dto.request.OrderRequest;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ApiResponse<OrderResponse> createOrder(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody OrderRequest request) {
        return orderService.createOrder(authHeader, request);
    }

//    @GetMapping("/{id}")
//    public void getOrderById() {
//
//    }
}
