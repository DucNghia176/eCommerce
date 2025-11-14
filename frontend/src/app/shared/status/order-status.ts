export enum OrderStatus {
  pending = 'PENDING',          // Chờ thanh toán / xác nhận
  confirmed = 'CONFIRMED',      // Đã xác nhận
  processing = 'PROCESSING',    // Đang chuẩn bị hàng
  shipping = 'SHIPPING',        // Đang vận chuyển
  delivered = 'DELIVERED',      // Đã giao thành công
  returned = 'RETURNED',        // Khách trả hàng
  cancelled = 'CANCELLED',      // Đã hủy
  failed = 'FAILED',            // Thanh toán thất bại / giao thất bại
  refunded = 'REFUNDED',        // Đã hoàn tiền
  on_hold = 'ON_HOLD',          // Tạm ngưng xử lý
  completed = 'COMPLETED'       // Hoàn thành đơn hàng
}

export const OrderStatusMeta: {
  [key in OrderStatus]: { label: string; color: string; bgColor: string }
} = {
  [OrderStatus.pending]: {
    label: 'Chờ xác nhận',
    color: '#f59e0b',  // cam đậm
    bgColor: '#fef3c7' // cam nhạt
  },
  [OrderStatus.confirmed]: {
    label: 'Đã xác nhận',
    color: '#3b82f6',  // xanh lam
    bgColor: '#dbeafe' // xanh lam nhạt
  },
  [OrderStatus.processing]: {
    label: 'Đang chuẩn bị hàng',
    color: '#8b5cf6',  // tím
    bgColor: '#ede9fe' // tím nhạt
  },
  [OrderStatus.shipping]: {
    label: 'Đang vận chuyển',
    color: '#06b6d4',  // xanh cyan
    bgColor: '#cffafe' // xanh cyan nhạt
  },
  [OrderStatus.delivered]: {
    label: 'Đã giao thành công',
    color: '#10b981',  // xanh lá
    bgColor: '#d1fae5' // xanh lá nhạt
  },
  [OrderStatus.returned]: {
    label: 'Đã trả hàng',
    color: '#0ea5e9',  // xanh dương đậm
    bgColor: '#e0f2fe' // xanh dương nhạt
  },
  [OrderStatus.cancelled]: {
    label: 'Đã hủy',
    color: '#ef4444',  // đỏ
    bgColor: '#fee2e2' // đỏ nhạt
  },
  [OrderStatus.failed]: {
    label: 'Thất bại',
    color: '#6b7280',  // xám đậm
    bgColor: '#f3f4f6' // xám nhạt
  },
  [OrderStatus.refunded]: {
    label: 'Đã hoàn tiền',
    color: '#14b8a6',  // teal
    bgColor: '#ccfbf1' // teal nhạt
  },
  [OrderStatus.on_hold]: {
    label: 'Tạm ngưng xử lý',
    color: '#a855f7',  // tím đậm
    bgColor: '#f3e8ff' // tím nhạt
  },
  [OrderStatus.completed]: {
    label: 'Hoàn tất',
    color: '#22c55e',  // xanh lá sáng
    bgColor: '#dcfce7' // xanh lá nhạt
  }
};

export const ORDER_STATUS_TRANSITIONS: Record<OrderStatus, OrderStatus[]> = {
  [OrderStatus.pending]: [
    OrderStatus.confirmed,
    OrderStatus.cancelled,
    OrderStatus.failed
  ],
  [OrderStatus.confirmed]: [
    OrderStatus.processing,
    OrderStatus.cancelled,
    OrderStatus.on_hold
  ],
  [OrderStatus.processing]: [
    OrderStatus.shipping,
    OrderStatus.cancelled,
    OrderStatus.on_hold
  ],
  [OrderStatus.shipping]: [
    OrderStatus.delivered,
    OrderStatus.failed,
    OrderStatus.returned
  ],
  [OrderStatus.delivered]: [
    OrderStatus.returned,
    OrderStatus.completed
  ],
  [OrderStatus.returned]: [
    OrderStatus.refunded
  ],
  [OrderStatus.failed]: [
    OrderStatus.refunded
  ],
  [OrderStatus.cancelled]: [
    OrderStatus.refunded
  ],
  [OrderStatus.on_hold]: [
    OrderStatus.processing,
    OrderStatus.cancelled
  ],
  [OrderStatus.refunded]: [],
  [OrderStatus.completed]: []
};


