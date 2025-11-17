import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CategoryRequest, CategoryResponse, ChildCategoryResponse} from "../models/category.model";
import {catchError, map, Observable, throwError} from "rxjs";
import {ApiResponse} from "../models/common.model";
import {Page} from "../models/page.model";
import {ProductSummary} from "../models/product-summary.model";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiUrl = environment.apiUrl + 'category';

  constructor(private http: HttpClient) {
  }

  createCategory(request: CategoryRequest, image ?: File): Observable<CategoryResponse> {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(request)], {type: 'application/json'}));
    if (image) {
      formData.append('image', image);
    }
    return this.http.post<ApiResponse<CategoryResponse>>(`${this.apiUrl}/create`, formData)
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

  getAllWithFeature(): Observable<ChildCategoryResponse[]> {
    return this.http.get<ApiResponse<ChildCategoryResponse[]>>(`${this.apiUrl}/all-with-featured`)
      .pipe(map(response => {
        if (response.code === 200 && response.data) {
          return response.data;
        }
        throw new Error(response.message);
      }));
  }

  getAllCategory(): Observable<CategoryResponse[]> {
    return this.http.get<ApiResponse<CategoryResponse[]>>(`${this.apiUrl}`)
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

  updateCategory(id: number, request: CategoryRequest, image ?: File): Observable<CategoryResponse> {
    const formData = new FormData();

    formData.append('request', new Blob([JSON.stringify(request)], {type: 'application/json'}));
    if (image) {
      formData.append('image', image);
    }
    return this.http.put<ApiResponse<CategoryResponse>>(`${this.apiUrl}/update/${id}`, formData)
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

  deleteCategory(id: number): Observable<CategoryResponse> {
    return this.http.delete<ApiResponse<CategoryResponse>>(`${this.apiUrl}/delete/${id}`)
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

  getCategoryById(id: number): Observable<CategoryResponse> {
    return this.http.get<ApiResponse<CategoryResponse>>(`${this.apiUrl}/${id}`)
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

  getProductsByCategory(id: number, page: number = 0, size: number = 10): Observable<Page<ProductSummary>> {
    const params: any = {
      page: page,
      size: size,
    };

    return this.http.get<ApiResponse<Page<ProductSummary>>>(`${this.apiUrl}/${id}/products`, {
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
