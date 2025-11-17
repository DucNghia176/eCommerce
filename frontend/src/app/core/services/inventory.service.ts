import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {InventoryRequest, InventoryResponse} from "../models/inventory.model";
import {catchError, map, Observable, throwError} from "rxjs";
import {ApiResponse} from "../models/common.model";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private apiUrl = environment.apiUrl + 'inventory';

  constructor(private http: HttpClient) {
  }

  updateInventory(request: InventoryRequest): Observable<InventoryResponse> {
    return this.http.post<ApiResponse<InventoryResponse>>(`${this.apiUrl}/update/quantity`, request)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        }),
        catchError(this.handleError)
      );
  }

  getQuantity(skuCode: string): Observable<number> {
    return this.http.get<{ code: number, data: number, message: string }>(
      `${this.apiUrl}/quantity`,
      {params: {skuCode}}
    ).pipe(
      map(response => {
        if (response.code === 200 && response.data != null) {
          return response.data; // number
        }
        throw new Error(response.message);
      })
    );
  }


  public handleError(error: any): Observable<never> {
    let errorMessage = 'Đã xảy ra lỗi';
    if (error.error instanceof ErrorEvent) {
      errorMessage = error.error.message;
    } else {
      errorMessage = error.error.message || `Mã lỗi: ${error.status}\nThông báo: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
