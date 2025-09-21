package ecommerce.orderservice.dto.request;

import ecommerce.apicommon1.model.status.PaymentMethodStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    private String shippingAddress;

    @NotEmpty(message = "Phải có ít nhất một sản phẩm trong đơn hàng")
    @Valid
    private List<OrderItemRequest> items;
    private PaymentMethodStatus paymentMethod;
    private String note;
    private boolean fromCart;
}
