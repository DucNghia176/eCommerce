import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {PaymentService} from "../../../../core/services/payment.service";
import {ToastService} from "../../../../core/services/toast.service";

@Component({
  selector: 'app-payment-cancel',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './payment-cancel.component.html',
  styleUrl: './payment-cancel.component.scss'
})
export class PaymentCancelComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private paymentService: PaymentService,
    private router: Router,
    private toast: ToastService
  ) {
  }

  ngOnInit() {
    const orderId = this.route.snapshot.queryParamMap.get('orderId');

    if (!orderId) {
      this.toast.show("Không xác định được đơn hàng", 'f');
      this.router.navigate(['/user/shopCard']);
      return;
    }

    this.paymentService.cancel(orderId).subscribe({
      next: () => {
        this.toast.show("Bạn đã hủy thanh toán!", 'f');
        this.router.navigate(['/user/shopCard']); // quay lại giỏ hàng
      },
      error: () => {
        this.toast.show("Không thể xử lý hủy thanh toán!", 'p');
        this.router.navigate(['/user/shopCard']);
      }
    });
  }
}
