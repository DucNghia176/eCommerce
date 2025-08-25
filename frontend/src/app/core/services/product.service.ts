import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, map, Observable, throwError} from "rxjs";
import {ProductRequest, ProductResponse, ProductSearchRequest} from "../models/product.model";
import {ApiResponse} from "../models/common.model";
import {Page} from "../models/page.model";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = 'http://localhost:8085/api/product';

  constructor(private http: HttpClient) {
  }

  createProduct(request: ProductRequest, images ?: File[]): Observable<ProductResponse> {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(request)], {type: 'application/json'}));
    if (images) {
      images.forEach(image => formData.append('images', image));
    }
    return this.http.post<ApiResponse<ProductResponse>>(`${this.apiUrl}/create`, formData)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        }), catchError(this.handleError)
      );
  }

  getAllProduct(page: number = 0, size: number = 10): Observable<Page<ProductResponse>> {
    const params: any = {
      page: page,
      size: size,
    };

    return this.http.get<ApiResponse<Page<ProductResponse>>>(`${this.apiUrl}`, {
      params
    }).pipe(
      map(response => {
        if (response.code === 200 && response.data) {
          return response.data;
        }
        throw new Error(response.message || 'Something went wrong');
      }),
      catchError(this.handleError)
    );
  }

  getProductById(id: number): Observable<ProductResponse> {
    return this.http.get<ApiResponse<ProductResponse>>(`${this.apiUrl}/${id}`)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message || 'Something went wrong');
        }),
        catchError(this.handleError)
      );
  }

  updateProduct(id: number, request: ProductRequest, images ?: File[]): Observable<ProductResponse> {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(request)], {type: 'application/json'}));
    if (images) {
      images.forEach(image => formData.append('images', image));
    }
    return this.http.put<ApiResponse<ProductResponse>>(`${this.apiUrl}/update/${id}`, formData)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        }), catchError(this.handleError)
      );
  }

  deleteProduct(id: number): Observable<ProductResponse> {
    return this.http.delete<ApiResponse<ProductResponse>>(`${this.apiUrl}/delete/${id}`)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message || 'Something went wrong');
        }),
        catchError(this.handleError)
      );
  }

  searchProduct(request: ProductSearchRequest, page: number = 0, size: number = 10): Observable<Page<ProductResponse>> {
    return this.http.post<ApiResponse<Page<ProductResponse>>>(`${this.apiUrl}/search`, request)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message || 'Something went wrong');
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
