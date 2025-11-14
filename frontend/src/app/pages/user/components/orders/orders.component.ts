import {Component, OnInit} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {DatePipe, DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {OrderResponse} from "../../../../core/models/orders.model";
import {OrderStatus, OrderStatusMeta} from "../../../../shared/status/order-status";
import {OrderSharedService} from "../../share/order-shared.service";

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    FormsModule,
    DatePipe,
    DecimalPipe,
    NgForOf,
    NgIf
  ],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.scss'
})
export class OrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  filteredOrders: OrderResponse[] = [];
  displayedOrders: OrderResponse[] = [];
  searchTerm: string = '';
  selectedStatus: string = 'all';
  // Pagination
  currentPage: number = 1;
  pageSize: number = 5;
  totalPages: number = 1;
  statusOptions = [
    {value: 'all', label: 'Tất cả'},
    {value: OrderStatus.pending, label: 'Chờ xác nhận'},
    {value: OrderStatus.confirmed, label: 'Đã xác nhận'},
    {value: OrderStatus.shipping, label: 'Đang giao hàng'},
    {value: OrderStatus.delivered, label: 'Đã giao'},
    {value: OrderStatus.cancelled, label: 'Đã hủy'},
    {value: OrderStatus.failed, label: 'Thất bại'},
    {value: OrderStatus.completed, label: 'Hoàn tất'}
  ];
  protected readonly OrderStatus = OrderStatus;
  protected readonly OrderStatusMeta = OrderStatusMeta;

  constructor(private orderSharedService: OrderSharedService) {
  }

  get pages(): number[] {
    return Array.from({length: this.totalPages}, (_, i) => i + 1);
  }

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.orderSharedService.orders$.subscribe((data) => {
      this.orders = data;
      this.filteredOrders = [...this.orders];
      this.updatePagination();
    });
    this.orderSharedService.loadOrders();
  }

  filterOrders() {
    this.filteredOrders = this.orders.filter(order => {
      const matchesSearch = order.id.toString().toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesStatus = this.selectedStatus === 'all' || order.status.toLowerCase() === this.selectedStatus.toLowerCase();
      return matchesSearch && matchesStatus;
    });
    this.currentPage = 1;
    this.updatePagination();
  }

  filterByStatus(status: string) {
    this.selectedStatus = status;
    this.filterOrders();
  }

  updatePagination() {
    this.totalPages = Math.ceil(this.filteredOrders.length / this.pageSize);
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.displayedOrders = this.filteredOrders.slice(startIndex, endIndex);
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  viewOrderDetails(order: OrderResponse) {
    console.log('Xem chi tiết:', order);
  }

  cancelOrder(order: OrderResponse) {
    if (confirm(`Bạn có chắc muốn hủy đơn hàng ${order.id}?`)) {
      order.status = OrderStatus.cancelled;
      this.orderSharedService.updateOrders(this.orders);
    }
  }
}
