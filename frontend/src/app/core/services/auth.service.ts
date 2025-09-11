import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, catchError, map, Observable, throwError} from "rxjs";
import {ApiResponse} from "../models/common.model";
import {AuthRequest, RegisterRequest} from "../models/auth.model";
import {UserResponse} from "../models/user.model";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8085/api/auth';

  private tokenSubject = new BehaviorSubject<string | null>(null);
  token$ = this.tokenSubject.asObservable();

  constructor(private http: HttpClient) {
  }

  login(auth: AuthRequest): Observable<ApiResponse<{ token: string }>> {
    return this.http.post<ApiResponse<{ token: string }>>(`${this.apiUrl}/login`, auth)
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

  logout() {
    this.clearToken();
  }

  register(request: RegisterRequest, avatar ?: File): Observable<ApiResponse<UserResponse>> {
    const formData = new FormData();

    formData.append('data', new Blob([JSON.stringify(request)], {type: 'application/json'}));
    return this.http.post<ApiResponse<UserResponse>>(`${this.apiUrl}/register`, formData).pipe(
      map(response => {
        if (response.code === 202 && response.data) {
          return response;
        }
        throw new Error(response.message || 'Đăng ký thất bại');
      }),
      catchError(this.handleError)
    );
  }

  sendOtp(email: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<any>>(`${this.apiUrl}/send`,
      null, {params: {email: email}}).pipe(
      map(response => {
        if (response.code === 200) return response;
        throw new Error(response.message || 'Gửi OTP thất bại');
      }),
      catchError(this.handleError)
    );
  }

  confirmRegister(email: string, otp: string): Observable<ApiResponse<UserResponse>> {
    return this.http.post<ApiResponse<UserResponse>>(
      `${this.apiUrl}/register/confirm`, null,
      {params: {email, otp}}
    ).pipe(
      map(response => {
        if (response.code === 200) return response;
        throw new Error(response.message || 'Xác thực OTP thất bại');
      }),
      catchError(this.handleError)
    );
  }


  verifyOtp(email: string, otp: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/verify`, null, {params: {email: email, otp: otp}}
    ).pipe(
      map(reponse => {
        if (reponse.code === 200) return reponse;
        throw new Error(reponse.message || 'Xác thực thất bại');
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
