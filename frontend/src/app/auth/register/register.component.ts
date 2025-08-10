import {Component, ElementRef, ViewChild} from '@angular/core';
import {CommonModule} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {AuthService} from "../../core/services/auth.service";
import {Router, RouterModule} from "@angular/router";
import {validateAndFocusFirstError} from "../../shared/utils/validation";
import {Gender} from "../../shared/status/gender";
import {RegisterRequest} from "../../core/models/auth.model";
import {ToastComponent} from "../../shared/components/toast/toast.component";
import {ToastService} from "../../core/services/toast.service";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ToastComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  username: string = '';
  password: string = '';
  fullName?: string = '';
  email: string = '';
  gender?: Gender;
  dateOfBirth?: Date;
  avatar?: File;
  today = new Date().toISOString().split('T')[0]
  avatarPreview?: string;
  @ViewChild('formRef') formRef!: ElementRef;
  protected readonly Gender = Gender;

  constructor(private authService: AuthService, private router: Router, private toastService: ToastService) {
  }


  onFileSelected(event: any) {
    const file = event.target.files?.[0];
    if (file) {
      this.avatar = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.avatarPreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  register(form: NgForm,) {
    if (!validateAndFocusFirstError(form, this.formRef)) return;

    const request: RegisterRequest = {
      username: this.username,
      password: this.password,
      fullName: this.fullName,
      email: this.email,
      gender: this.gender,
      dateOfBirth: this.dateOfBirth,
    }

    this.authService.register(request, this.avatar).subscribe({
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
}
