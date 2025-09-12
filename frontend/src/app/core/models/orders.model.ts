import {OrderStatus} from "../../shared/status/order-status";
import {PaymentStatus} from "../../shared/status/payment-status";

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
