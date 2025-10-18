import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, map, Observable, tap} from "rxjs";
import {ApiResponse} from "../models/common.model";
import {Cart} from "../models/cart.model";

@Injectable({
  providedIn: 'root'
})
export class CartService {

  baseUrl = 'http://localhost:8085/api/cart';
  private cartSubject = new BehaviorSubject<Cart | null>(null);
  cart$ = this.cartSubject.asObservable();

  constructor(private http: HttpClient) {
  }

  getCart(): void {
    this.http.get<ApiResponse<Cart>>(this.baseUrl).pipe(
      map(response => {
        if (response.code === 200 && response.data) {
          return response.data;
        }
        throw new Error(response.message);
      })
    ).subscribe({
      next: (cart) => this.cartSubject.next(cart),
      error: (err) => console.error('Lỗi tải giỏ hàng:', err)
    });
  }

  addToCart(productId: number, quantity: number): Observable<Cart> {
    return this.http.post<ApiResponse<Cart>>(`${this.baseUrl}/create`, {
      productId,
      quantity,
    }).pipe(
      map(response => {
        if (response.code === 200 && response.data) {
          return response.data;
        }
        throw new Error(response.message);
      }),
      tap(cart => this.cartSubject.next(cart)),
    );
  }

  updateCart(productId: number, quantity: number): Observable<Cart> {
    return this.http.post<ApiResponse<Cart>>(`${this.baseUrl}/update`, {
      productId,
      quantity,
    }).pipe(
      map(response => {
        if (response.code === 200 && response.data) {
          return response.data;
        }
        throw new Error(response.message);
      }),
      tap(cart => this.cartSubject.next(cart)),
    );
  }

  removeProducts(productIds: number[]): Observable<Cart> {
    return this.http.post<ApiResponse<Cart>>(`${this.baseUrl}/remove`, {
      productIds,
    }).pipe(
      map(response => {
        if (response.code === 200 && response.data) {
          return response.data;
        }
        throw new Error(response.message);
      }),
      tap(cart => this.cartSubject.next(cart)),
    );
  }
}
