import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private apiUrl = 'http://localhost:8085/api/payment';

  constructor(private http: HttpClient) {
  }

  checkout(params: { orderId: number, amount: number }): Observable<string> {
    const queryParams = new HttpParams()
      .set('orderId', params.orderId)
      .set('amount', params.amount);

    return this.http.post(`${this.apiUrl}/checkout`, {}, {
      params: queryParams,
      responseType: 'text'
    });
  }
}
