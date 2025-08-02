import {Component, ElementRef, ViewChild} from '@angular/core';
import {CommonModule} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {AuthService} from "../../core/services/auth.service";
import {Router, RouterModule} from "@angular/router";
import {UserRequest} from "../../core/models/user.model";
import {validateAndFocusFirstError} from "../../shared/utils/validation";
import {Gender} from "../../shared/status/gender";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
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
  errorMessages: string | null = null;
  today = new Date().toISOString().split('T')[0]
  avatarPreview?: string;
  @ViewChild('formRef') formRef!: ElementRef;
  protected readonly Gender = Gender;

  constructor(private authService: AuthService, private router: Router) {
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

  register(form: NgForm) {
    console.log('✅ Hàm register đã được gọi');
    console.log('Form valid:', form.valid);
    console.log('Form values:', form.value);
    if (!validateAndFocusFirstError(form, this.formRef)) return;

    const request: UserRequest = {
      username: this.username,
      password: this.password,
      fullName: this.fullName,
      email: this.email,
      gender: this.gender,
      dateOfBirth: this.dateOfBirth,
    }

    this.authService.register(request, this.avatar).subscribe({
      next: () => {
        console.log('Gửi thành công');
        alert('Đăng ký thành công, gửi OTP...');
        console.log('Gọi sendOtpAndRedirect');
        this.sendOtpAndRedirect();
      },
      error: (err) => {
        this.errorMessages = err.message || 'Đăng ký thất bại';
      }
    });
  }

  sendOtpAndRedirect() {
    this.authService.sendOtp(this.email).subscribe({
      next: () => {
        console.log('Gửi OTP thành công');
        this.router.navigate(['/auth/confirm'], {
          queryParams: {email: this.email, flow: 'register'}
        });
      },
      error: (err) => {
        this.errorMessages = 'Gửi OTP thất bại: ' + err.message;
      }
    });
  }
}
