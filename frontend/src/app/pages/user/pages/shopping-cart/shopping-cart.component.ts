import {Component, inject, OnInit} from '@angular/core';
import {Cart, CartItem} from "../../../../core/models/cart.model";
import {CartService} from "../../../../core/services/cart.service";
import {faArrowLeft} from "@fortawesome/free-solid-svg-icons";
import {FormsModule} from "@angular/forms";
import {CartTotalComponent} from "../../components/cart-total/cart-total.component";
import {CartDetailComponent} from "../../components/cart-detail/cart-detail.component";
import {ToastService} from "../../../../core/services/toast.service";
import {ToastComponent} from "../../../../shared/components/toast/toast.component";

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [
    FormsModule,
    CartTotalComponent,
    CartDetailComponent,
    ToastComponent
  ],
  templateUrl: './shopping-cart.component.html',
  styleUrl: './shopping-cart.component.scss'
})
export class ShoppingCartComponent implements OnInit {
  cart: Cart | null = null;
  cartItems: (CartItem & { selected?: boolean })[] = [];
  subtotal = 0;
  selectedItems: (CartItem & { selected?: boolean })[] = [];
  protected readonly faArrowLeft = faArrowLeft;
  private cartService = inject(CartService);
  private toastService = inject(ToastService);

  onOrderCreated() {
    this.cartService.getCart();
    this.toastService.show('Đơn hàng của bạn đã được tạo thành công!', 'p');
  }

  ngOnInit(): void {
    this.cartService.cart$.subscribe(cart => {
      this.cart = cart;
      this.cartItems = cart?.items.map(item => ({...item, selected: false})) || []
    });
    this.cartService.getCart();
  }

  setTotal($event: number | 0) {
    this.subtotal = $event;
  }

  setSelectedItems($event: (CartItem & { selected?: boolean })[]) {
    this.selectedItems = $event;
  }
}
