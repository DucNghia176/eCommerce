package ecommerce.orderservice.controller;

import ecommerce.apicommon.model.response.ApiResponse;
import ecommerce.orderservice.dto.request.OrderRequest;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

//    @GetMapping("/{id}")
//    public void getOrderById() {
//
//    }
}
