import {Injectable} from '@angular/core';
import {BrandResponse, TagResponse} from "../models/TagBrand.model";
import {catchError, map, Observable, throwError} from "rxjs";
import {ApiResponse} from "../models/common.model";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class TagBrandService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  getAllBrand(): Observable<BrandResponse[]> {
    return this.http.get<ApiResponse<BrandResponse[]>>(`${this.apiUrl}/brand`)
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

  getAllTag(): Observable<TagResponse[]> {
    return this.http.get<ApiResponse<TagResponse[]>>(`${this.apiUrl}/tag`)
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

  private handleError(error: any): Observable<never> {
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
