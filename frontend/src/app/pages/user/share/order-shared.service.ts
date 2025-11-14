import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {OrderResponse} from "../../../core/models/orders.model";
import {OrdersService} from "../../../core/services/orders.service";

@Injectable({
  providedIn: 'root'
})
export class OrderSharedService {

  private ordersSubject = new BehaviorSubject<OrderResponse[]>([]);
  orders$ = this.ordersSubject.asObservable();

  constructor(private ordersService: OrdersService) {
  }

  loadOrders(): void {
    this.ordersService.getOrderByUser().subscribe({
      next: (data) => this.ordersSubject.next(data),
      error: (err) => {
        console.error('Lỗi khi tải đơn hàng:', err);
        this.ordersSubject.next([]);
      }
    });
  }

  updateOrders(orders: OrderResponse[]): void {
    this.ordersSubject.next(orders);
  }
}
