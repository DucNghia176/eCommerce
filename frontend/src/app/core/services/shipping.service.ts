import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ShippingService {

  private baseUrl = 'http://localhost:8085/api/shipments';

  constructor(private http: HttpClient) {
  }

  /** 📦 Lấy danh sách đơn hàng đang giao */
  getShippingOrders(): Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }

  /** 🧱 Lấy lịch sử blockchain của 1 đơn */
  getOrderHistory(orderId: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/${orderId}/history`);
  }

  /** ✅ Xác nhận đã nhận hàng */
  confirmReceived(orderId: string, userId: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/${orderId}/confirm`, {userId});
  }
}
