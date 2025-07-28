import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, catchError, map, Observable, throwError} from "rxjs";
import {ApiResponse} from "../models/common.model";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8085/auth';

  private tokenSubject = new BehaviorSubject<string | null>(null);
  token$ = this.tokenSubject.asObservable();

  constructor(private http: HttpClient) {
  }

  login(usernameOrEmail: string, password: string): Observable<ApiResponse<{ token: string }>> {
    return this.http.post<ApiResponse<{ token: string }>>(`${this.apiUrl}/login`, {usernameOrEmail, password})
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response;
          }
          throw new Error(response.message || 'Đăng nhập thất bại');
        }),
        catchError(this.handleError)
      );
  }

  setToken(token: string) {
    this.tokenSubject.next(token);
    localStorage.setItem('token', token); // lưu nếu cần giữ lâu dài
  }

  getToken(): string | null {
    return this.tokenSubject.value || localStorage.getItem('token');
  }

  clearToken() {
    this.tokenSubject.next(null);
    localStorage.removeItem('token');
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
