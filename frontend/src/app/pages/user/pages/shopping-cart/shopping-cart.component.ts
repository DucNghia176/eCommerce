import {Component, inject, OnInit} from '@angular/core';
import {Cart, CartItem} from "../../../../core/models/cart.model";
import {CartService} from "../../../../core/services/cart.service";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faArrowLeft, faArrowRight, faChevronLeft, faTrash} from "@fortawesome/free-solid-svg-icons";
import {CurrencyPipe, NgForOf} from "@angular/common";
import {sum} from "ng-zorro-antd/core/util";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [
    FaIconComponent,
    NgForOf,
    CurrencyPipe,
    FormsModule
  ],
  templateUrl: './shopping-cart.component.html',
  styleUrl: './shopping-cart.component.scss'
})
export class ShoppingCartComponent implements OnInit {
  cart: Cart | null = null;
  cartItems: (CartItem & { selected?: boolean })[] = [];
  subtotal = 0;
  selectedSubtotal = 0;
  isAllSelected = false;
  protected readonly faChevronLeft = faChevronLeft;
  protected readonly faArrowLeft = faArrowLeft;
  protected readonly faArrowRight = faArrowRight;
  protected readonly sum = sum;
  protected readonly faTrash = faTrash;
  private cartService = inject(CartService);

  get selectedItems() {
    return this.cartItems.filter(item => item.selected);
  }

  get subtotalSelected() {
    return this.selectedItems.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0);
  }

  ngOnInit(): void {
    this.cartService.cart$.subscribe(cart => {
      this.cart = cart;
      this.cartItems = cart?.items.map(item => ({...item, selected: false})) || []
      this.calculateSubtotal();
      this.updateSubtotal();
    });
    this.cartService.getCart();
  }

  increaseQuantity(item: any) {
    item.quantity++;
    this.updateSubtotal();
  }

  decreaseQuantity(item: any) {
    if (item.quantity > 1) {
      item.quantity--;
      this.updateSubtotal();
    }
  }

  removeFromCart(item: CartItem) {
    this.cartService.removeProducts([item.productId]).subscribe({
      next: () => {
        // Xóa khỏi danh sách hiển thị
        this.cartItems = this.cartItems.filter(i => i.productId !== item.productId);
        // Cập nhật tổng tiền
        this.updateSubtotal();
      },
      error: err => {
        console.error('Lỗi khi xóa sản phẩm:', err);
      }
    });
  }


  toggleSelectAll(event: any) {
    this.isAllSelected = event.target.checked;
    this.cartItems.forEach(i => i.selected = this.isAllSelected);
    this.updateSelectedItems();
  }

  updateSelectedItems() {
    this.isAllSelected = this.cartItems.every(i => i.selected);
    this.updateSelectedSubtotal();
  }

  updateSubtotal() {
    this.subtotal = this.cartItems.reduce((sum, i) => sum + i.unitPrice * i.quantity, 0);
    this.updateSelectedSubtotal();
  }

  updateSelectedSubtotal() {
    this.selectedSubtotal = this.cartItems
      .filter(i => i.selected)
      .reduce((sum, i) => sum + i.unitPrice * i.quantity, 0);
  }

  private calculateSubtotal(): void {
    this.subtotal = this.cartItems.reduce(
      (sum, item) => sum + item.unitPrice * item.quantity,
      0
    );
  }
}
