package ecommerce.apicommon1.model.status;

public enum OrderStatus {
    PENDING,      // Chờ thanh toán / xác nhận
    CONFIRMED,    // Đã xác nhận
    PROCESSING,   // Đang chuẩn bị hàng
    SHIPPING,     // Đang vận chuyển
    DELIVERED,    // Đã giao thành công
    RETURNED,     // Khách trả hàng
    CANCELLED,    // Đã hủy
    FAILED,       // Thanh toán thất bại / giao thất bại
    REFUNDED,     // Đã hoàn tiền
    ON_HOLD       // Tạm ngưng xử lý
}