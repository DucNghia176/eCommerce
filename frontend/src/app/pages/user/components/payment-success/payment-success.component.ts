import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {PaymentService} from "../../../../core/services/payment.service";
import {ToastService} from "../../../../core/services/toast.service";
import {CurrencyPipe, DatePipe, NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-payment-success',
  standalone: true,
  imports: [
    DatePipe,
    CurrencyPipe,
    NgForOf,
    NgIf
  ],
  templateUrl: './payment-success.component.html',
  styleUrl: './payment-success.component.scss'
})
export class PaymentSuccessComponent implements OnInit {
  loading = true;
  orderId?: number;
  order: any;

  constructor(
    private route: ActivatedRoute,
    private paymentService: PaymentService,
    private router: Router,
    private toast: ToastService
  ) {
  }

  ngOnInit() {
    const orderId = this.route.snapshot.queryParamMap.get('orderId');
    const sessionId = this.route.snapshot.queryParamMap.get('session_id');

    if (!orderId || !sessionId) {
      this.toast.show("Thiếu thông tin thanh toán!", 'f');
      this.router.navigate(['/user/shopCard']);
      return;
    }

    this.paymentService.confirm(orderId, sessionId).subscribe({
      next: () => {
        this.toast.show("Thanh toán thành công!", 'p');
        this.router.navigate(['/user/dashboard/orders']);
      },
      error: () => {
        this.toast.show("Không thể xác nhận thanh toán!", 'f');
        this.router.navigate(['/user/shopCard']);
      }
    });
  }

  goToOrders() {
    this.router.navigate(['/user/dashboard/orders']);
  }

  continueShopping() {
    this.router.navigate(['/user/shopCard']);
  }
}
