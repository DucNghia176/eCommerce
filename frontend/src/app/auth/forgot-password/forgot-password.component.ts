import {Component, ElementRef, ViewChild} from '@angular/core';
import {CommonModule} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {AuthService} from "../../core/services/auth.service";
import {Router, RouterModule} from "@angular/router";
import {validateAndFocusFirstError} from "../../shared/utils/validation";

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss'
})
export class ForgotPasswordComponent {
  email: string = '';
  errorMessages: string | null = null;
  @ViewChild('formRef') formRef!: ElementRef;

  constructor(private authService: AuthService, private router: Router) {
  }

  resetPassword(form: NgForm) {
    if (!validateAndFocusFirstError(form, this.formRef)) return;

    this.authService.sendOtp(this.email).subscribe({
      next: () => {
        this.router.navigate(['/auth/confirm'], {
          queryParams: {email: this.email, flow: 'forgot'}
        });
      },
      error: (err) => {
        this.errorMessages = 'Gửi OTP thất bại: ' + err.message;
      }
    });
  }
}
