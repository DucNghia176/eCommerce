import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CategoryRequest, CategoryResponse} from "../models/category.model";
import {catchError, map, Observable, throwError} from "rxjs";
import {ApiResponse} from "../models/common.model";

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiUrl = 'http://localhost:8085/api/category';

  constructor(private http: HttpClient) {
  }

  createCategory(data: CategoryRequest): Observable<CategoryRequest> {
    const formData = new FormData();
    formData.append('name', data.name);
    if (data.parentId !== undefined && data.parentId !== null) {
      formData.append('parentId', data.parentId.toString());
    }
    if (data.image) {
      formData.append('image', data.image); // image là File
    }
    return this.http.post<ApiResponse<CategoryRequest>>(`${this.apiUrl}/create`, formData)
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
