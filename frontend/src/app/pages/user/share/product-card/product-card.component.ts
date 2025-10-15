import {Component, Input} from '@angular/core';
import {NzModalModule, NzModalService} from "ng-zorro-antd/modal";
import {Router} from "@angular/router";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faCartPlus, faHeart} from "@fortawesome/free-solid-svg-icons";
import {faEye} from "@fortawesome/free-solid-svg-icons/faEye";
import {NzRateComponent} from "ng-zorro-antd/rate";
import {FormsModule} from "@angular/forms";
import {CurrencyPipe} from "@angular/common";
import {ProductDetailComponent} from "../../components/product-detail/product-detail.component";
import {AuthModalService} from "../../../../shared/service/auth-modal.service";

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
export class ProductCardComponent {
  @Input() product: any;
  isLoggedIn = false;
  @Input() viewMode: 'modal' | 'page' = 'modal';
  protected readonly faHeart = faHeart;
  protected readonly faCartPlus = faCartPlus;
  protected readonly faEye = faEye;

  constructor(private modal: NzModalService, private router: Router, private authModal: AuthModalService,) {
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
    }
  }

  addToWishlist(product: any) {
    if (!this.isLoggedIn) {
      this.authModal.openModal();
      return;
    }
  }
}
