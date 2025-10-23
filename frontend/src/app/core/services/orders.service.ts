import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {Page} from "../models/page.model";
import {OrderAD, OrderCreateRequest, OrderCreateResponse} from "../models/orders.model";
import {ApiResponse} from "../models/common.model";

@Injectable({
  providedIn: 'root'
})
export class OrdersService {
  private apiUrl = 'http://localhost:8085/api/orders';

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
    return this.http.get<ApiResponse<Page<OrderAD>>>(`${this.apiUrl}/orders`, {
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
}
