import {Component} from '@angular/core';
import {CommonModule} from "@angular/common";
import {FormsModule, NgModel} from "@angular/forms";
import {AuthService} from "../../core/services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent {
  email: string = '';

  constructor(authService: AuthService, private router: Router) {
  }

  resetPassword(emailRef: NgModel) {
    emailRef.control.markAsTouched();
    if (emailRef.invalid) {
      return; // không gửi nếu email chưa hợp lệ
    }
    this.router.navigate(['/auth/confirm'])
    // Gửi yêu cầu hoặc chuyển trang
    console.log('Sending reset for', this.email);
    // this.router.navigate(['/auth/confirm']);
  }
}
