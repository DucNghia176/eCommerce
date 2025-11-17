import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, map, Observable, throwError} from 'rxjs';
import {UserOrderDetail, UserOrdersResponse, UserResponse, UserUpdateRequest} from '../models/user.model';
import {ApiResponse} from "../models/common.model";
import {Page} from "../models/page.model";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  private apiUrl = environment.apiUrl + 'users';

  constructor(private http: HttpClient) {
  }

  getUserById(): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.apiUrl}/{id}`)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        }), catchError(this.handleError)
      )
  }

  // getAllUsers(page: number = 0, size: number = 10, isLock?: number): Observable<Page<UserResponse>> {
  //   // Dùng object rỗng rồi thêm params nếu có
  //   const params: any = {
  //     page: page,
  //     size: size,
  //   };
  //
  //   if (isLock !== undefined && isLock !== null) {
  //     params.isLock = isLock;
  //   }
  //
  //   return this.http.get<ApiResponse<Page<UserResponse>>>(`${this.apiUrl}/all`, {
  //     params
  //   }).pipe(
  //     map(response => {
  //       if (response.code === 200 && response.data) {
  //         return response.data;
  //       }
  //       throw new Error(response.message || 'Something went wrong');
  //     }),
  //     catchError(this.handleError)
  //   );
  // }

  getAllUsers1(page: number = 0, size: number = 10): Observable<Page<UserOrdersResponse>> {
    const params: any = {
      page: page,
      size: size,
    };

    return this.http.get<ApiResponse<Page<UserOrdersResponse>>>(`${this.apiUrl}/all/1`, {
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

  userOrderDetail(userId: number): Observable<UserOrderDetail> {
    return this.http.get<ApiResponse<UserOrderDetail>>(`${this.apiUrl}/order/${userId}`)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data
          }
          throw new Error(response.message);
        })
      )
  }

  updateUser(request: UserUpdateRequest, avatar ?: File): Observable<UserResponse> {
    const formData = new FormData();

    formData.append('data', new Blob([JSON.stringify(request)], {type: 'application/json'}));
    if (avatar) {
      formData.append('avatar', avatar);
    }

    return this.http.put<ApiResponse<UserResponse>>(`${this.apiUrl}/update`, formData)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message || 'Cập nhật thất bại');
        }), catchError(this.handleError)
      )
  }

  toggleLock(id: string): Observable<UserResponse> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.apiUrl}/toggle/lock/${id}`, null)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message);
        }), catchError(this.handleError)
      )
  }

  toggleLock1(id: string): Observable<ApiResponse<UserResponse>> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.apiUrl}/toggle/lock/${id}`, null)
  }


  toggleRole(id: string): Observable<UserResponse> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.apiUrl}/toggle/role/${id}`, null)
      .pipe(
        map(response => {
          if (response.code === 200 && response.data) {
            return response.data;
          }
          throw new Error(response.message || 'Đổi quyền thất bại');
        }), catchError(this.handleError)
      )
  }

  // count(): Observable<CountResponse> {
  //   return this.http.get<ApiResponse<CountResponse>>(`${this.apiUrl}/count`)
  //     .pipe(
  //       map(response => {
  //         if (response.code === 200 && response.data) {
  //           return response.data;
  //         }
  //         throw new Error(response.message || 'Không thể lấy dữ liệu thống kê người dùng');
  //       })
  //     )
  // }

  deleteUser(id: number): Observable<UserResponse> {
    return this.http.delete<ApiResponse<UserResponse>>(`${this.apiUrl}/${id}`)
      .pipe(
        map(res => {
          if (res.code === 200 && res.data) {
            return res.data;
          }
          throw new Error(res.message);
        })
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
