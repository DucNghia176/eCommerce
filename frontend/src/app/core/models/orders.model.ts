import {OrderStatus} from "../../shared/status/order-status";
import {PaymentStatus} from "../../shared/status/payment-status";
import {PaymentMethodStatus} from "../../shared/status/payment-method-status";

export interface OrderAD {
  id: number;
  orderCode: string;
  orderDate: Date;
  customerId: number;
  customerName: string;
  paymentStatus: PaymentStatus;
  orderStatus: OrderStatus;
  orderAmount: string;
  formattedDate: string;
  paymentMethod: string;
}

export interface OrderCreateRequest {
  shippingAddress: string;
  items: OrderItemRequest[];
  paymentMethod: PaymentMethodStatus;
  note: string;
  fromCart: boolean;
}

export interface OrderCreateResponse {
  id: number;
  userId: number;
  shippingAddress: string;
  totalAmount: string;
  status: string;
  createdAt: Date;
  orderDetails: OrderDetailResponse[];
}

export interface OrderDetailResponse {
  productId: number;
  quantity: number;
  unitPrice: string;
  discount: string;
  createdAt: Date;
}

export interface OrderItemRequest {
  productId: number;
  quantity: number;
}
