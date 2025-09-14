import {Gender} from "../../shared/status/gender";
import {OrderStatus} from "../../shared/status/order-status";

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  fullName: string;
  dateOfBirth: Date;
  gender: string;
  isLock: number;
  roles: string[];
  avatar: string;
  createdAt: Date;
  updatedAt: Date;
  address: string;
  phone: string;
}

export interface UserOrdersResponse {
  id: number;
  name: string;
  avatar: string;
  address: string;
  totalOrders: number;
  totalAmount: string;
  isLock: number;
}

export interface UserRequest {
  username: string;
  password: string;
  fullName?: string;
  email: string;
  gender?: string;
  dateOfBirth?: Date;
}

export interface UserUpdateRequest {
  fullName: string;
  gender: Gender;
  dateOfBirth: Date;
}

export interface CountResponse {
  all: number;
  active: number;
  inactive: number;
}

export interface UserOrderDetail {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  address: string;
  isLock: number;
  avatar: string;
  daysJoined: string;
  userOrderDetailResponse: UserOrderDetailResponse[]
}

export interface UserOrderDetailResponse {
  orderId: number;
  orderCode: string;
  orderDate: string;
  orderStatus: OrderStatus
  orderPrice: string
  formattedDate: string
}
