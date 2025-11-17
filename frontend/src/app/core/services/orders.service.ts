import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {Page} from "../models/page.model";
import {
  OrderAD,
  OrderCreateRequest,
  OrderCreateResponse,
  OrderResponse,
  UpdateOrderStatusRequest,
  UpdateOrderStatusResponse
} from "../models/orders.model";
import {ApiResponse} from "../models/common.model";
import {OrderStatus} from "../../shared/status/order-status";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class OrdersService {
  private apiUrl = environment.apiUrl + 'orders';

  constructor(private http: HttpClient) {
  }

  createOrder(request: OrderCreateRequest): Observable<OrderCreateResponse> {
    return this.http.post<ApiResponse<OrderCreateResponse>>(`${this.apiUrl}/create`, request)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        })
      )
  }

  getAll(page: number = 0, size: number = 0): Observable<Page<OrderAD>> {
    const params: any = {page: page, size: size};
    return this.http.get<ApiResponse<Page<OrderAD>>>(`${this.apiUrl}`, {
      params
    }).pipe(
      map((response) => {
        if (response.code === 200 && response.data) {
          return response.data
        }
        throw new Error(response.message);
      })
    );
  }

  getOrderByUser(): Observable<OrderResponse[]> {
    return this.http.get<ApiResponse<OrderResponse[]>>(`${this.apiUrl}/my-order`)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        })
      );
  }

  // order.service.ts
  updateOrderStatus(orderId: number, orderStatus: OrderStatus): Observable<UpdateOrderStatusResponse> {
    const payload: UpdateOrderStatusRequest = {
      orderId,
      orderStatus
    };

    return this.http.put<ApiResponse<UpdateOrderStatusResponse>>(
      `${this.apiUrl}/update-status`,
      payload
    )
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data; // { orderId, orderStatus }
          }
          throw new Error(response.message || 'Cập nhật trạng thái đơn hàng thất bại');
        })
      );
  }

}
