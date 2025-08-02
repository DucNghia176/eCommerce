import {Component, ElementRef, ViewChild} from '@angular/core';
import {CommonModule} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {AuthService} from "../../core/services/auth.service";
import {Router} from "@angular/router";
import {UserRequest} from "../../core/models/user.model";
import {validateAndFocusFirstError} from "../../shared/utils/validation";
import {Gender} from "../../shared/status/gender";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
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
  @ViewChild('formRef') formRef!: ElementRef;
  protected readonly Gender = Gender;

  constructor(private authService: AuthService, private router: Router) {
  }

  onFileSelected(event: any) {
    const file = event.target.files?.[0];
    if (file) {
      this.avatar = file;
    }
  }

  register(form: NgForm) {
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
      next: (request) => {
        alert('Đăng ký thành công');
        this.router.navigate(['/auth/login']);
      },
      error: (err) => {
        this.errorMessages = err.message || 'Đăng ký thất bại';
      }
    });
  }
}
