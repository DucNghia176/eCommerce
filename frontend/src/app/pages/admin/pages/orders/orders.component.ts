import {Component, inject, OnInit} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {
  faAdd,
  faCheck,
  faCheckCircle,
  faClipboardCheck,
  faFileExport,
  faMoneyBillWave,
  faTrash
} from "@fortawesome/free-solid-svg-icons";
import {SelectionService} from "../../../../core/services/selection.service";
import {PageSize} from "../../../../shared/status/page-size";
import {PaymentStatus, PaymentStatusMeta} from "../../../../shared/status/payment-status";
import {OrderStatusMeta} from "../../../../shared/status/order-status";
import {OrdersService} from "../../../../core/services/orders.service";
import {PageComponent} from "../../../../shared/components/page/page.component";
import {CurrencyPipe, NgForOf, NgIf} from "@angular/common";
import {StatusDirective} from "../../../../shared/directive/status.directive";
import {OrderAD} from "../../../../core/models/orders.model";
import {PaymentMethodStatus} from "../../../../shared/status/payment-method-status";


@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [
    PageComponent,
    FaIconComponent,
    NgIf,
    NgForOf,
    StatusDirective,
    CurrencyPipe
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
  protected readonly faAdd = faAdd;
  protected readonly faFileExport = faFileExport;
  protected readonly PaymentStatusMeta = PaymentStatusMeta;
  protected readonly OrderStatusMeta = OrderStatusMeta;
  protected readonly faCheck = faCheck;
  protected readonly faCheckCircle = faCheckCircle;
  protected readonly faClipboardCheck = faClipboardCheck;
  protected readonly faTrash = faTrash;
  protected readonly faMoneyBillWave = faMoneyBillWave;
  protected readonly PaymentStatus = PaymentStatus;
  protected readonly PaymentMethodStatus = PaymentMethodStatus;
  private orderService = inject(OrdersService);

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
}
