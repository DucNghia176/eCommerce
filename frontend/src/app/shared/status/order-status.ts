export enum OrderStatus {
  pending = 'PENDING',
  confirmed = 'CONFIRMED',
  shipping = 'SHIPPING',
  delivered = 'DELIVERED',
  cancelled = 'CANCELLED',
  failed = 'FAILED'
}

export const OrderStatusMeta: {
  [key in OrderStatus]: { label: string; color: string; bgColor: string }
} = {
  [OrderStatus.pending]: {label: 'Chờ xác nhận', color: '#f59e0b', bgColor: '#fef3c7'},
  [OrderStatus.confirmed]: {label: 'Đã xác nhận', color: '#3b82f6', bgColor: '#dbeafe'},
  [OrderStatus.shipping]: {label: 'Đang giao', color: '#06b6d4', bgColor: '#cffafe'},
  [OrderStatus.delivered]: {label: 'Đã giao', color: '#10b981', bgColor: '#d1fae5'},
  [OrderStatus.cancelled]: {label: 'Đã hủy', color: '#ef4444', bgColor: '#fee2e2'},
  [OrderStatus.failed]: {label: 'Thất bại', color: '#6b7280', bgColor: '#f3f4f6'}
};

