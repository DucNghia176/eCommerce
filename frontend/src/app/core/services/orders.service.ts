import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {Page} from "../models/page.model";
import {OrderAD} from "../models/orders.model";
import {ApiResponse} from "../models/common.model";

@Injectable({
  providedIn: 'root'
})
export class OrdersService {
  private apiUrl = 'http://localhost:8085/api/orders';

  constructor(private http: HttpClient) {
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
}
