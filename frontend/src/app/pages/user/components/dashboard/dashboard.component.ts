import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {DatePipe, DecimalPipe, NgForOf} from "@angular/common";
import {OrderResponse} from "../../../../core/models/orders.model";
import {OrderSharedService} from "../../share/order-shared.service";
import {OrderStatus, OrderStatusMeta} from "../../../../shared/status/order-status";

interface OrderStats {
  totalOrders: number;
  pendingOrders: number;
  completedOrders: number;
  totalSpent: number;
}

interface RecentOrder {
  id: string;
  orderDate: Date;
  status: OrderStatus;
  total: number;
  itemCount: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    NgForOf,
    DecimalPipe,
    DatePipe
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  orders: OrderResponse[] = [];
  stats!: OrderStats;
  recentOrders: RecentOrder[] = [];
  protected readonly OrderStatusMeta = OrderStatusMeta;

  constructor(private router: Router, private orderSharedService: OrderSharedService) {
  }

  ngOnInit() {
    this.orderSharedService.orders$.subscribe((data) => {
      this.orders = data;
      this.calculateStats();
    });

    this.orderSharedService.loadOrders();
  }

  calculateStats() {
    this.stats = {
      totalOrders: this.orders.length,
      pendingOrders: this.orders.filter(o => o.status === 'PENDING').length,
      completedOrders: this.orders.filter(o => o.status === 'DELIVERED').length,
      totalSpent: this.orders.reduce((sum, o) => sum + Number(o.totalAmount), 0)
    };

    this.recentOrders = this.orders
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 5)
      .map(o => ({
        id: o.orderCode,
        orderDate: o.createdAt,
        status: o.status,
        total: Number(o.totalAmount),
        itemCount: o.orderDetails.length
      }));
  }

  viewAllOrders() {
    this.router.navigate(['user/dashboard/orders']);
  }

  viewOrderDetail(orderId: string) {
    console.log('View order:', orderId);
  }
}
