import {Component, inject, OnInit} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faAdd, faClipboardCheck, faFileExport, faMoneyBillWave, faTruck} from "@fortawesome/free-solid-svg-icons";
import {SelectionService} from "../../../../core/services/selection.service";
import {PageSize} from "../../../../shared/status/page-size";
import {PaymentStatus, PaymentStatusMeta} from "../../../../shared/status/payment-status";
import {ORDER_STATUS_TRANSITIONS, OrderStatus, OrderStatusMeta} from "../../../../shared/status/order-status";
import {OrdersService} from "../../../../core/services/orders.service";
import {PageComponent} from "../../../../shared/components/page/page.component";
import {CurrencyPipe, NgForOf, NgIf} from "@angular/common";
import {StatusDirective} from "../../../../shared/directive/status.directive";
import {OrderAD} from "../../../../core/models/orders.model";
import {PaymentMethodStatus} from "../../../../shared/status/payment-method-status";
import {FormsModule} from "@angular/forms";
import {ToastService} from "../../../../core/services/toast.service";


@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    PageComponent,
    FaIconComponent,
    NgIf,
    NgForOf,
    StatusDirective,
    CurrencyPipe,
    FormsModule
  ],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.scss'
})
export class OrdersComponent implements OnInit {
  orders: OrderAD[] = [];
  totalPages = 0;
  currentPage = 0;
  totalItems = 0;
  pageSize: number = 10;
  pageSizes: number[] = PageSize
  public selectionService = inject(SelectionService<number>);
  OrderStatus = OrderStatus;
  ORDER_STATUS_TRANSITIONS = ORDER_STATUS_TRANSITIONS;
  protected readonly faAdd = faAdd;
  protected readonly faFileExport = faFileExport;
  protected readonly PaymentStatusMeta = PaymentStatusMeta;
  protected readonly OrderStatusMeta = OrderStatusMeta;
  protected readonly faClipboardCheck = faClipboardCheck;
  protected readonly faMoneyBillWave = faMoneyBillWave;
  protected readonly PaymentStatus = PaymentStatus;
  protected readonly PaymentMethodStatus = PaymentMethodStatus;
  protected readonly faTruck = faTruck;
  private orderService = inject(OrdersService);
  private toastService = inject(ToastService);

  ngOnInit() {
    this.loadOrders();
  }

  public loadOrders(page: number = 0) {
    this.orderService.getAll(page, this.pageSize)
      .subscribe({
        next: data => {
          const orders = data.content;
          this.currentPage = data.number;
          this.totalItems = data.totalElements;
          this.totalPages = data.totalPages;
          this.orders = orders;
        },
        error: err => {
          this.orders = []
        }
      })
  }

  onPageSizeChange(newSize: number) {
    this.pageSize = newSize;
    this.loadOrders(0);
  }

  onCheckbox(id: number, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    const ids = this.orders.map(order => order.id);
    this.selectionService.toggleSelection(id, checked, ids);
  }

  onCheckboxAll(event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    const ids = this.orders.map(order => order.id);
    this.selectionService.toggleAll(checked, ids);
  }

  getAllowedNextStatuses(current: OrderStatus): OrderStatus[] {
    return this.ORDER_STATUS_TRANSITIONS[current] ?? [];
  }

  canChangeTo(current: OrderStatus, target: OrderStatus): boolean {
    return this.getAllowedNextStatuses(current).includes(target);
  }

  updateOrderStatus(order: any, nextStatus: OrderStatus) {
    if (!this.canChangeTo(order.orderStatus, nextStatus)) {
      // Không cho bắn request sai
      // show toast cảnh báo nếu muốn
      return;
    }

    this.orderService.updateOrderStatus(order.id, nextStatus).subscribe({
      next: (res) => {
        order.orderStatus = res.orderStatus as OrderStatus;
        this.toastService.show("Cập nhật trạng thái thành công", "p")
      },
      error: (err) => {
        this.toastService.show("Cập nhật trạng thái thất bại", "f")
      }
    });
  }

  confirmOrder(order: any) {
    this.updateOrderStatus(order, OrderStatus.confirmed);
  }

  // Ví dụ nút “Giao hàng”
  shipOrder(order: any) {
    this.updateOrderStatus(order, OrderStatus.shipping);
  }

  confirmPaid(order: any) {
    this.updateOrderStatus(order, OrderStatus.confirmed);
  }
}
