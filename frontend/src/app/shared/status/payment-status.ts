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
  [PaymentStatus.Paid]: {label: 'Paid', color: '#10b981', bgColor: '#d1fae5'},
  [PaymentStatus.Pending]: {label: 'Pending', color: '#f59e0b', bgColor: '#fef3c7'},
  [PaymentStatus.Success]: {label: 'Success', color: '#3b82f6', bgColor: '#dbeafe'},
  [PaymentStatus.Failed]: {label: 'Failed', color: '#ef4444', bgColor: '#fee2e2'},
  [PaymentStatus.Refunded]: {label: 'Refunded', color: '#06b6d4', bgColor: '#cffafe'},
  [PaymentStatus.Cancelled]: {label: 'Cancelled', color: '#6b7280', bgColor: '#f3f4f6'}
};
