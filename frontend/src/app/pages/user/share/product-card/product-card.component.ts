import {Component, inject, Input, OnInit} from '@angular/core';
import {NzModalModule, NzModalService} from "ng-zorro-antd/modal";
import {Router} from "@angular/router";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCartPlus} from "@fortawesome/free-solid-svg-icons";
import {faEye} from "@fortawesome/free-solid-svg-icons/faEye";
import {NzRateComponent} from "ng-zorro-antd/rate";
import {FormsModule} from "@angular/forms";
import {CurrencyPipe} from "@angular/common";
import {ProductDetailComponent} from "../../components/product-detail/product-detail.component";
import {AuthModalService} from "../../../../shared/service/auth-modal.service";
import {AuthService} from "../../../../core/services/auth.service";
import {CartService} from "../../../../core/services/cart.service";

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [
    FaIconComponent,
    NzRateComponent,
    FormsModule,
    CurrencyPipe,
    NzModalModule
  ],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.scss'
})
export class ProductCardComponent implements OnInit {
  @Input() product: any;
  isLoggedIn = false;
  @Input() quantity: number = 1;
  @Input() viewMode: 'modal' | 'page' = 'modal';
  protected readonly faCartPlus = faCartPlus;
  protected readonly faEye = faEye;
  private cartService = inject(CartService);

  constructor(private modal: NzModalService, private router: Router, private authModal: AuthModalService, private authService: AuthService) {
  }

  ngOnInit() {
    this.authService.isAuthenticated$.subscribe(status => {
      this.isLoggedIn = status;
    });
  }

  viewDetail(product: any) {
    if (this.viewMode === 'modal') {
      // Mở modal
      const modalRef = this.modal.create({
        nzTitle: product.name,
        nzContent: ProductDetailComponent, // component hiển thị trong modal
        nzFooter: null,
        nzWidth: '80%',
        nzWrapClassName: 'custom-modal'
      });
      modalRef.getContentComponent().product = product;
    } else if (this.viewMode === 'page') {
      // Điều hướng đến trang chi tiết
      this.router.navigate(['/products', product.id]);
    }
  }


  addToCart(product: any) {
    if (!this.isLoggedIn) {
      this.authModal.openModal();
      return;
    } else {
      this.addToCartDetail(product.id, this.quantity);
    }
  }

  addToCartDetail(productId: number, quantity: number) {
    this.cartService.addToCart(productId, quantity).subscribe({});
  }

}
