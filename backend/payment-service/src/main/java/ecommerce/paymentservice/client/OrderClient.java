package ecommerce.paymentservice.client;

import ecommerce.apicommon1.model.request.UpdateOrderStatusRequest;
import ecommerce.apicommon1.model.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service", path = "/api/orders")
public interface OrderClient {
    @PutMapping("/update/status")
    ApiResponse<UpdateOrderStatusRequest> updateOrderStatus(@RequestBody UpdateOrderStatusRequest request);
}
