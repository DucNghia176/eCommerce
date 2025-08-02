import {Component} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../core/services/auth.service";

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

  constructor(private authService: AuthService, private router: Router, private route: ActivatedRoute) {
    this.route.queryParams.subscribe(params => {
      this.email = params['email'] || '';
      this.flow = params['flow'] || 'register';
    });
  }

  confirmEmail() {
    if (!this.code || !this.email) {
      this.errorMessage = 'Thiếu mã xác thực';
      return;
    }

    if (this.flow === 'register') {
      this.confirmRegister();
    } else if (this.flow === 'forgot') {
      this.confirmForgot();
    }
  }

  resendCode() {
    this.authService.sendOtp(this.email).subscribe({
      next: () => {
        alert('Gửi lại email thành công.');
      },
      error: (err: any) => {
        this.errorMessage = err.message || 'Gửi mã email thất bại.';
      }
    });
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
