import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {CurrencyPipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {Cart, CartItem} from "../../../../core/models/cart.model";
import {CartService} from "../../../../core/services/cart.service";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faTrash} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-cart-detail',
  standalone: true,
  imports: [CurrencyPipe, FormsModule, NgForOf, NgIf, FaIconComponent],
  templateUrl: './cart-detail.component.html',
  styleUrls: ['./cart-detail.component.scss']
})
export class CartDetailComponent {
  @Input() cart: Cart | null = null;
  @Input() cartItems: (CartItem & { selected?: boolean })[] = [];
  @Output() selectedSubtotal = new EventEmitter<number | 0>();
  @Output() selectedItemsChange = new EventEmitter<(CartItem & { selected?: boolean })[]>();

  isAllSelected = false;
  subtotal = 0;
  protected readonly faTrash = faTrash;
  private cartService = inject(CartService);

  get hasSelectedItems(): boolean {
    return this.cartItems.some(i => i.selected);
  }

  toggleSelectAll(event: any) {
    this.isAllSelected = event.target.checked;
    this.cartItems.forEach(i => i.selected = this.isAllSelected);
    this.updateSelectedItems();
  }

  changeQuantity(item: CartItem, delta: number) {
    const newQty = item.quantity + delta;
    if (newQty < 1) return;
    item.quantity = newQty;
    this.updateQuantity(item.productId, newQty);
    this.updateSubtotal();
  }

  removeSelectedItems() {
    const idsToRemove = this.cartItems.filter(i => i.selected).map(i => i.productId);
    this.cartService.removeProducts(idsToRemove).subscribe({
      next: () => {
        this.cartItems = this.cartItems.filter(i => !idsToRemove.includes(i.productId));
        this.updateSubtotal();
      },
      error: err => console.error('Lỗi khi xóa sản phẩm:', err)
    });
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
    const total = this.cartItems.filter(i => i.selected)
      .reduce((sum, i) => sum + i.unitPrice * i.quantity, 0);
    this.selectedSubtotal.emit(total);
    this.selectedItemsChange.emit(this.cartItems.filter(i => i.selected));
  }

  updateQuantity(productId: number, quantity: number) {
    this.cartService.updateCart(productId, quantity).subscribe({
      next: () => {
        this.cartItems = this.cartItems.map(i => i.productId === productId ? {...i, quantity} : i);
      },
      error: err => console.error('Lỗi cập nhật số lượng:', err)
    });
  }
}
