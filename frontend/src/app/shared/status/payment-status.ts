export enum PaymentStatus {
  Paid = 'PAID',
  Pending = 'PENDING',
  Success = 'SUCCESS',
  Failed = 'FAILED',
  Refunded = 'REFUNDED',
  Cancelled = 'CANCELLED'
}

export const PaymentStatusMeta: {
  [key in PaymentStatus]: { label: string; color: string; bgColor?: string };
} = {
  [PaymentStatus.Paid]: {
    label: 'Đã thanh toán',
    color: '#10b981',
    bgColor: '#d1fae5'
  },
  [PaymentStatus.Pending]: {
    label: 'Chờ thanh toán',
    color: '#f59e0b',
    bgColor: '#fef3c7'
  },
  [PaymentStatus.Success]: {
    label: 'Thanh toán thành công',
    color: '#3b82f6',
    bgColor: '#dbeafe'
  },
  [PaymentStatus.Failed]: {
    label: 'Thanh toán thất bại',
    color: '#ef4444',
    bgColor: '#fee2e2'
  },
  [PaymentStatus.Refunded]: {
    label: 'Đã hoàn tiền',
    color: '#06b6d4',
    bgColor: '#cffafe'
  },
  [PaymentStatus.Cancelled]: {
    label: 'Thanh toán đã hủy',
    color: '#6b7280',
    bgColor: '#f3f4f6'
  }
};
