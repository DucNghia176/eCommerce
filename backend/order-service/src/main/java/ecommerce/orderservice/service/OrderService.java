package ecommerce.orderservice.service;

import ecommerce.aipcommon.model.response.ApiResponse;
import ecommerce.aipcommon.model.response.UserOrderDetailResponse;
import ecommerce.orderservice.dto.request.OrderRequest;
import ecommerce.orderservice.dto.response.OrderResponse;
import ecommerce.orderservice.dto.response.OrdersAD;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface OrderService {
    ApiResponse<OrderResponse> placeOrder(OrderRequest request);

    ApiResponse<Page<OrdersAD>> getOrders(int page, int size);

    Map<Long, Long> extractOrderQuantity(List<Long> usersId);

    List<UserOrderDetailResponse> getUserOrderDetail(Long id);
}
