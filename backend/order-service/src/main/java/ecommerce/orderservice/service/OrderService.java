package ecommerce.orderservice.service;


import ecommerce.apicommon1.model.request.UpdateOrderStatusRequest;
import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.response.UpdateOrderStatusResponse;
import ecommerce.apicommon1.model.response.UserOrderDetailResponse;
import ecommerce.orderservice.dto.request.OrderCreateRequest;
import ecommerce.orderservice.dto.request.OrderRequest;
import ecommerce.orderservice.dto.response.OrderCreateResponse;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.dto.response.OrdersAD;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface OrderService {
    ApiResponse<OrderResponse> placeOrder(OrderRequest request);

    OrderCreateResponse order(OrderCreateRequest request);

    ApiResponse<Page<OrdersAD>> getOrders(int page, int size);

    Map<Long, Long> extractOrderQuantity(List<Long> usersId);

    List<UserOrderDetailResponse> getUserOrderDetail(Long id);

    UpdateOrderStatusResponse updateOrderStatus(UpdateOrderStatusRequest request);

    boolean existsByUserIdAndProductIdAndStatus(Long userId, Long productId);
}
