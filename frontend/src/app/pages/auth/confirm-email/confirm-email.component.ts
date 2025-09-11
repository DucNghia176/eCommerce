import {Component} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../../core/services/auth.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-confirm-email',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './confirm-email.component.html',
  styleUrl: './confirm-email.component.scss'
})
export class ConfirmEmailComponent {
  code: string = '';
  email: string = '';
  flow: 'register' | 'forgot' = 'register';
  errorMessage: string | null = null;
  resendSuccess = '';
  resendCooldown = 0;
  toastMessage: string | null = null;
  toastType: 'success' | 'error' | null = null;
  resendInterval: any;

  constructor(private authService: AuthService, private router: Router, private route: ActivatedRoute, private toastr: ToastrService) {
    this.route.queryParams.subscribe(params => {
      this.email = params['email'] || '';
      this.flow = params['flow'] || 'register';
    });
  }

  confirmEmail() {
    if (!this.code || !this.email) {
      this.errorMessage = 'Thiếu mã xác thực';
      this.toastr.error(this.errorMessage)
      return;
    }

    if (this.flow === 'register') {
      this.confirmRegister();
    } else if (this.flow === 'forgot') {
      this.confirmForgot();
    }
  }

  resendCode() {
    if (this.resendCooldown > 0) return;

    this.authService.sendOtp(this.email).subscribe({
      next: () => {
        this.toastr.success('Mã xác nhận đã được gửi lại thành công!')
        this.resendSuccess = 'Mã xác nhận đã được gửi lại!';
        this.errorMessage = null;
        this.startCooldown();
      },
      error: (err: any) => {
        this.errorMessage = err.message || 'Gửi mã email thất bại.';
      }
    });
  }

  ngOnDestroy() {
    if (this.resendInterval) {
      clearInterval(this.resendInterval);
    }
  }

  startCooldown() {
    this.resendCooldown = 60; // 60 giây đếm ngược
    this.resendInterval = setInterval(() => {
      this.resendCooldown--;
      if (this.resendCooldown === 0) {
        clearInterval(this.resendInterval);
      }
    }, 1000);
  }

  private confirmRegister() {
    this.authService.confirmRegister(this.email, this.code).subscribe({
      next: () => {
        alert('Xác thực thành công. Vui lòng đăng nhập.');
        this.router.navigate(['/auth/login']);
      },
      error: (err: any) => {
        this.errorMessage = err.message || 'Xác thực thất bại.';
      }
    });
  }

  private confirmForgot() {
    this.authService.verifyOtp(this.email, this.code).subscribe({
      next: () => {
        alert('Xác thực thành công. Hãy đặt lại mật khẩu.');
        this.router.navigate(['/auth/reset-password'], {
          queryParams: {email: this.email}
        });
      },
      error: (err: any) => {
        this.errorMessage = err.message || 'Xác thực thất bại.';
      }
    });
  }
}
