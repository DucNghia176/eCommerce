import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {CurrencyPipe, NgForOf} from "@angular/common";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faArrowRight} from "@fortawesome/free-solid-svg-icons";
import {OrdersService} from "../../../../core/services/orders.service";
import {OrderCreateRequest, OrderItemRequest} from "../../../../core/models/orders.model";
import {PaymentMethodStatus} from "../../../../shared/status/payment-method-status";
import {CartItem} from "../../../../core/models/cart.model";
import {ToastService} from "../../../../core/services/toast.service";
import {FormsModule} from "@angular/forms";
import {AddressModalComponent} from "../address-modal/address-modal.component";
import {NzModalService} from "ng-zorro-antd/modal";
import {NzButtonComponent} from "ng-zorro-antd/button";

interface SavedAddress {
  id: number;
  address: string;
}

@Component({
  selector: 'app-cart-total',
  standalone: true,
  imports: [
    CurrencyPipe,
    FaIconComponent,
    FormsModule,
    NgForOf,
    NzButtonComponent
  ],
  templateUrl: './cart-total.component.html',
  styleUrl: './cart-total.component.scss'
})
export class CartTotalComponent {
  @Input() subTotal = 0;
  @Input() items: (CartItem & { selected?: boolean })[] = [];
  shippingAddress = '';
  paymentMethod = PaymentMethodStatus.COD;
  note = '';
  fromCart: boolean = true;
  @Output() orderCreated = new EventEmitter<void>();
  selectedAddressId: number | null = null;
  savedAddresses: SavedAddress[] = [];
  protected readonly faArrowRight = faArrowRight;
  protected readonly PaymentMethodStatus = PaymentMethodStatus;
  private orderService = inject(OrdersService);
  private toastService = inject(ToastService);

  constructor(private modal: NzModalService) {
    const saved = JSON.parse(localStorage.getItem('savedAddresses') || '[]');
    this.savedAddresses = saved.map((addr: string, index: number) => ({id: index + 1, address: addr}));
  }

  createOrder() {
    const orderItems: OrderItemRequest[] = this.items.map(i => ({
      productId: i.productId,
      quantity: i.quantity
    }));

    const request: OrderCreateRequest = {
      shippingAddress: this.shippingAddress,
      items: orderItems,
      paymentMethod: this.paymentMethod,
      note: this.note,
      fromCart: this.fromCart,
    }
    this.orderService.createOrder(request).subscribe({
      next: res => {
        this.toastService.show('Đặt hàng thành công', 'p');
        this.orderCreated.emit();
      },
      error: err => {
        this.toastService.show('Đặt hàng thất bại', 'f')
      }
    })
  }

  openAddressModal(): void {
    const modalRef = this.modal.create({
      nzTitle: 'Thêm địa chỉ giao hàng mới',
      nzContent: AddressModalComponent,
      nzFooter: null,
      nzWidth: 600
    });

    const instance = modalRef.getContentComponent();
    instance.onSave.subscribe((newAddress: any) => {
      const id = this.savedAddresses.length + 1;
      this.savedAddresses.push({id, address: newAddress.fullAddress});
      this.selectedAddressId = id;
      this.shippingAddress = newAddress.fullAddress;
      modalRef.close();
    });
  }

  onSelectAddress(id: number) {
    const addr = this.savedAddresses.find(a => a.id === id);
    if (addr) this.shippingAddress = addr.address;
  }
}
