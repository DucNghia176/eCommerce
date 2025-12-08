import {inject} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest} from '@angular/common/http';
import {AuthService} from '../services/auth.service';
import {catchError, Observable, throwError} from 'rxjs';

export const AuthInterceptor: HttpInterceptorFn = (
  request: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {

  const authService = inject(AuthService);
  const token = authService.getToken();

  if (token) {
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {

      // Không kết nối được server
      if (error.status === 0) {
        return throwError(() => error);
      }

      // Server lỗi
      if (error.status >= 500) {
        return throwError(() => error);
      }

      // Không xử lý logout cho login/register
      const isAuthRequest =
        request.url.includes('/auth/login') ||
        request.url.includes('/auth/register') ||
        request.url.includes('/auth/register/confirm');

      if (isAuthRequest) {
        return throwError(() => error);
      }

      // ❗ NEW FIX: Nếu đang ở trang /user → KHÔNG REDIRECT nữa
      const currentUrl = window.location.pathname;
      const isLoginPage = currentUrl.startsWith('/user');

      if (isLoginPage) {
        return throwError(() => error);
      }

      // Nếu token hết hạn thật sự → logout & redirect
      if (error.status === 401) {
        console.warn('⚠️ Token hết hạn hoặc không hợp lệ — auto logout');
        authService.clearToken();
        window.location.href = '/user';
      }

      return throwError(() => error);
    })
  );
};
