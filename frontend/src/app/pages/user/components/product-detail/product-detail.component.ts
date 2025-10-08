import {Component, ElementRef, inject, Input, OnInit, ViewChild} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {CurrencyPipe, NgClass, NgForOf} from "@angular/common";
import {faArrowLeft, faArrowRight, faCartPlus, faMinus, faPlus} from "@fortawesome/free-solid-svg-icons";
import {NzRateComponent} from "ng-zorro-antd/rate";
import {FormsModule} from "@angular/forms";
import {ProductByTagResponse} from "../../../../core/models/product.model";
import {PaymentService} from "../../../../core/services/payment.service";

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    FaIconComponent,
    NgForOf,
    NgClass,
    NzRateComponent,
    FormsModule,
    CurrencyPipe
  ],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {
  @Input() product!: ProductByTagResponse;
  @ViewChild('scrollContainer', {static: true}) scrollContainer!: ElementRef;
  selectedIndex: number = 0;
  quantity = 1;
  protected readonly faArrowRight = faArrowRight;
  protected readonly faArrowLeft = faArrowLeft;
  protected readonly faPlus = faPlus;
  protected readonly faMinus = faMinus;
  protected readonly faCartPlus = faCartPlus;
  private paymentService = inject(PaymentService);

  get selectedImage(): string {
    return this.product.imageUrls[this.selectedIndex];
  }

  ngOnInit() {
    if (this.product.imageUrls && this.product.imageUrls.length) {
      this.selectedIndex = 0;
    }
  }

  checkout() {
    const amount = Number(this.product.price.toString().replace(/,/g, '')) * this.quantity; // bỏ dấu phẩy và ép kiểu
    const params = {
      orderId: 3,
      amount: amount
    }
    this.paymentService.checkout(params).subscribe({
      next: (paymentUrl) => {
        console.log('Thanh toán thành công, redirect:', paymentUrl);
        window.location.href = paymentUrl; // ví dụ redirect sang PayPal hoặc VNPay
      },
      error: (err) => {
        console.error('Checkout lỗi:', err);
      }
    });
  }

  selectImage(index: number) {
    this.selectedIndex = index;
  }

  scrollLeft() {
    const container = this.scrollContainer.nativeElement;
    const newPos = container.scrollLeft - 200;
    container.scrollTo({left: newPos, behavior: 'smooth'});
    if (this.selectedIndex > 0) {
      this.selectedIndex--;
    }
  }

  scrollRight() {
    const container = this.scrollContainer.nativeElement;
    const newPos = container.scrollLeft + 200;
    container.scrollTo({left: newPos, behavior: 'smooth'});
    if (this.selectedIndex < this.product.imageUrls.length - 1) {
      this.selectedIndex++;
    }
  }

  increase() {
    this.quantity++;
  }

  decrease() {
    if (this.quantity > 1) this.quantity--;
  }
}
