import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {ApiResponse} from "../models/common.model";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private apiUrl = 'http://localhost:8085/api/payment';

  constructor(private http: HttpClient) {
  }

  checkout(params: { orderId: number, amount?: number }): Observable<string> {
    return this.http.post<ApiResponse<{ checkoutUrl: string }>>(`${this.apiUrl}/checkout`, params).pipe(
      map(response => {
        if (response.code === 200 && response.data?.checkoutUrl) {
          return response.data.checkoutUrl;
        }
        throw new Error(response.message || 'Không thể tạo phiên thanh toán');
      })
    );
  }

  confirm(orderId: string, sessionId: string) {
    const params = new HttpParams()
      .set('orderId', orderId)
      .set('session_id', sessionId);

    return this.http.get(`${this.apiUrl}/success`, {params});
  }

  cancel(orderId: string) {
    const params = new HttpParams().set('orderId', orderId);
    return this.http.get(`${this.apiUrl}/cancel`, {params});
  }
}
