import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, catchError, map, Observable, of, throwError} from "rxjs";
import {ApiResponse} from "../models/common.model";
import {AuthRequest, RegisterRequest} from "../models/auth.model";
import {UserResponse} from "../models/user.model";
import {environment} from "../../../environments/environment";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = environment.apiUrl + 'auth';

  private tokenSubject = new BehaviorSubject<string | null>(null);
  token$ = this.tokenSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private tokenCheckInterval: any;

  constructor(private http: HttpClient, private router: Router) {
    const token = localStorage.getItem('token');
    if (token) {
      this.tokenSubject.next(token);
      this.isAuthenticatedSubject.next(true);
      this.startTokenMonitor();
    }
  }

  login(auth: AuthRequest): Observable<ApiResponse<{ token: string }>> {
    return this.http.post<ApiResponse<{ token: string }>>(`${this.apiUrl}/login`, auth)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            this.setToken(response.data.token);
            this.isAuthenticatedSubject.next(true);
            this.startTokenMonitor();
            return response;
          }
          throw new Error(response.message || 'Đăng nhập thất bại');
        }),
        catchError(this.handleError)
      );
  }

  logout(): Observable<ApiResponse<string>> {
    const token = this.getToken();
    if (!token) {
      this.clearToken();
      this.isAuthenticatedSubject.next(false);
      return of({code: 200, message: "Đã logout", data: null});
    }

    return this.http.post<ApiResponse<string>>(
      `${this.apiUrl}/logout`,
      {},
      {headers: {Authorization: `Bearer ${token}`}}
    ).pipe(
      map(response => {
        this.clearToken();
        this.isAuthenticatedSubject.next(false);
        this.router.navigate(['/user']);
        setTimeout(() => {
          window.location.reload();
        }, 50);// ✔ chuyển trang đúng
        return response;
      }),
      catchError(err => {
        // ✔ vẫn xoá token dù BE lỗi
        this.clearToken();
        this.isAuthenticatedSubject.next(false);
        this.router.navigate(['/user']);
        setTimeout(() => {
          window.location.reload();
        }, 50);
        return throwError(() => err);
      })
    );
  }


  startTokenMonitor() {
    if (this.tokenCheckInterval) clearInterval(this.tokenCheckInterval);
    this.tokenCheckInterval = setInterval(() => {
      const token = this.getToken();
      if (token) {
        try {
          const payload = JSON.parse(atob(token.split('.')[1]));
          const expTime = payload.exp * 1000;
          const now = Date.now();

          if (expTime <= now) {
            console.warn("⚠️ Token đã hết hạn, tiến hành logout...");
            this.logout();
            clearInterval(this.tokenCheckInterval);
            window.location.href = '/user';
          }
        } catch (err) {
          console.error("Token không hợp lệ:", err);
          this.logout();
          clearInterval(this.tokenCheckInterval);
        }
      }
    }, 1000);
  }

  register(request: RegisterRequest): Observable<ApiResponse<UserResponse>> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.apiUrl}/register`, request).pipe(
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
    localStorage.setItem('token', token);
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
