package ecommerce.orderservice.util;

import ecommerce.apicommon1.model.status.OrderStatus;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusUtil {
    public boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case PENDING ->
                // Chờ thanh toán/xác nhận => xác nhận hoặc hủy
                    next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED || next == OrderStatus.FAILED;

            case CONFIRMED ->
                // Đã xác nhận => chuyển sang chuẩn bị hàng, hủy hoặc tạm ngưng
                    next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED || next == OrderStatus.ON_HOLD;

            case PROCESSING ->
                // Đang chuẩn bị hàng => chuyển sang giao, hủy hoặc tạm ngưng
                    next == OrderStatus.SHIPPING || next == OrderStatus.CANCELLED || next == OrderStatus.ON_HOLD;

            case SHIPPING ->
                // Đang vận chuyển => giao thành công, giao thất bại, trả lại
                    next == OrderStatus.DELIVERED || next == OrderStatus.FAILED || next == OrderStatus.RETURNED;

            case DELIVERED ->
                // Đã giao thành công => có thể phát sinh trả hàng hoặc kết thúc
                    next == OrderStatus.RETURNED || next == OrderStatus.COMPLETED;

            case RETURNED ->
                // Đã trả hàng => hoàn tiền
                    next == OrderStatus.REFUNDED;

            case FAILED, CANCELLED ->
                // Thất bại hoặc đã hủy => có thể hoàn tiền
                    next == OrderStatus.REFUNDED;

            case ON_HOLD ->
                // Tạm ngưng => có thể quay lại xử lý hoặc hủy
                    next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED;

            case REFUNDED, COMPLETED ->
                // Đã hoàn tiền hoặc đã hoàn tất => trạng thái cuối cùng
                    false;
        };
    }
}
