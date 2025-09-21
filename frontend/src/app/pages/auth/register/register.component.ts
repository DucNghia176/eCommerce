import {Component, ElementRef, ViewChild} from '@angular/core';
import {CommonModule} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {Router, RouterModule} from "@angular/router";
import {finalize} from "rxjs";
import {ToastComponent} from "../../../shared/components/toast/toast.component";
import {LoadingSpinnerComponent} from "../../../shared/components/loading-spinner/loading-spinner.component";
import {Gender} from "../../../shared/status/gender";
import {AuthService} from "../../../core/services/auth.service";
import {ToastService} from "../../../core/services/toast.service";
import {validateAndFocusFirstError} from "../../../shared/utils/validation";
import {RegisterRequest} from "../../../core/models/auth.model";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ToastComponent, LoadingSpinnerComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  username: string = '';
  password: string = '';
  email: string = '';
  isLoading = false;
  today = new Date().toISOString().split('T')[0]
  avatarPreview?: string;
  @ViewChild('formRef') formRef!: ElementRef;
  protected readonly Gender = Gender;

  constructor(private authService: AuthService, private router: Router, private toastService: ToastService) {
  }

  register(form: NgForm,) {
    if (!validateAndFocusFirstError(form, this.formRef)) return;

    const request: RegisterRequest = {
      username: this.username,
      password: this.password,
      email: this.email,
    }
    this.isLoading = true;
    this.authService.register(request)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.toastService.show("Đăng ký thành công.", "p")
          this.sendOtpAndRedirect();
        },
        error: (err) => {
          this.toastService.show(`Đăng ký thất bại + ${err.message}`, "f")
        }
      });
  }

  sendOtpAndRedirect() {
    this.authService.sendOtp(this.email).subscribe({
      next: () => {
        this.router.navigate(['/auth/confirm'], {
          queryParams: {email: this.email, flow: 'register'}
        });
      },
      error: (err) => {
        this.toastService.show("Gửi OTP thất bại: " + err.message, "f");
      }
    });
  }

  loginWithGoogle(forceSelect: boolean = false) {
    const baseUrl = 'http://localhost:8085/oauth2/authorization/google';
    window.location.href = forceSelect ? `${baseUrl}?forceSelect=true` : baseUrl;
  }

  loginWithFacebook(forceSelect: boolean = false) {
    const baseUrl = 'http://localhost:8085/oauth2/authorization/facebook';
    window.location.href = forceSelect
      ? `${baseUrl}?auth_type=reauthenticate`
      : baseUrl;
  }
}
