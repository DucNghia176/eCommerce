import {Component, OnInit} from '@angular/core';
import {ShippingService} from "../../../../core/services/shipping.service";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {OrderStatus, OrderStatusMeta} from "../../../../shared/status/order-status";

export interface OrderHistoryRecord {
  status: OrderStatus | string;   // enum hoặc fallback string
  location: string;
  timestamp: string;
  actor?: string;                 // tùy backend có gửi
}

/** Đơn hàng đang giao từ blockchain */
export interface ShippingOrder {
  shipmentID: string;
  orderId?: string;
  orderCode?: string;
  cargo: string;                  // ví dụ: "3x iPhone, 1x MacBook"
  userId: string;                  // MSP hoặc userId
  currentLocation: string;
  status: OrderStatus;
  lastUpdated: string;            // ISO string
  shippingAddress?: string;
  totalAmount?: number;
  updatesHistory?: OrderHistoryRecord[];
}

@Component({
  selector: 'app-shipping',
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
    DatePipe
  ],
  templateUrl: './shipping.component.html',
  styleUrl: './shipping.component.scss'
})
export class ShippingComponent implements OnInit {
  shippingOrders: ShippingOrder[] = [];
  selectedOrder: ShippingOrder | null = null;
  orderHistory: OrderHistoryRecord[] = [];
  loading = false;
  loadingHistory = false;

  protected readonly OrderStatusMeta = OrderStatusMeta;

  constructor(private shippingService: ShippingService) {
  }

  ngOnInit(): void {
    this.loadShippingOrders();
  }

  /** 📦 Lấy danh sách đơn hàng đang giao */
  loadShippingOrders(): void {
    this.loading = true;
    this.shippingService.getShippingOrders().subscribe({
      next: (res: any) => {
        this.shippingOrders = res.data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('❌ Lỗi khi tải danh sách vận chuyển:', err);
        this.loading = false;
      }
    });
  }

  /** Xem lịch sử blockchain */
  viewHistory(order: any): void {
    console.log('🟢 Xem lịch sử đơn hàng:', order);

    this.selectedOrder = order;
    this.loadingHistory = true;

    this.shippingService.getOrderHistory(order.shipmentID || order.orderId).subscribe({
      next: (res: any) => {
        console.log('📜 Kết quả lịch sử:', res);
        this.orderHistory = res.data || res;
        this.loadingHistory = false;
      },
      error: (err) => {
        console.error('❌ Lỗi khi lấy lịch sử:', err);
        this.loadingHistory = false;
      }
    });
  }


  /** ✅ Xác nhận đã nhận hàng */
  confirmReceived(order: any): void {
    if (!confirm(`Xác nhận bạn đã nhận đơn hàng ${order.shipmentID || order.orderCode}?`)) return;

    this.shippingService.confirmReceived(order.shipmentID || order.orderId, order.userId).subscribe({
      next: () => {
        alert('📦 Bạn đã xác nhận đã nhận hàng!');
        this.loadShippingOrders();
      },
      error: (err) => {
        alert('❌ Không thể xác nhận: ' + (err.error?.error || err.message));
      }
    });
  }

  /** Đóng modal */
  closeModal(): void {
    this.selectedOrder = null;
    this.orderHistory = [];
  }

  private mapFabricStatusToFrontend(fabricStatus: string): OrderStatus {
    const normalized = fabricStatus?.trim()?.toUpperCase();
    switch (normalized) {
      case 'CREATED':
        return OrderStatus.pending;
      case 'SHIPPING':
        return OrderStatus.shipping;
      case 'DELIVERED':
        return OrderStatus.delivered;
      case 'CANCELLED':
        return OrderStatus.cancelled;
      default:
        return OrderStatus.failed;
    }
  }
}
