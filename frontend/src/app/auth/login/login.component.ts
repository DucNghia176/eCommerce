import {Component, ElementRef, ViewChild} from '@angular/core';
import {FormsModule, NgForm} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {Router} from "@angular/router";
import {AuthService} from "../../core/services/auth.service";
import {AuthRequest} from "../../core/models/auth.model";
import {validateAndFocusFirstError} from "../../shared/utils/validation";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessages: string | null = null;
  @ViewChild('formRef') formRef!: ElementRef;

  constructor(private authService: AuthService, private router: Router) {
  }

  login(form: NgForm) {
    if (!validateAndFocusFirstError(form, this.formRef)) return;
    
    const auth: AuthRequest = {
      usernameOrEmail: this.username,
      password: this.password
    };

    this.authService.login(auth).subscribe({
      next: (response) => {
        if (response.data?.token) {

          const token = response.data.token;
          this.authService.setToken(token);

          const payload = JSON.parse(atob(token.split('.')[1]));
          const role = payload.role;

          if (role === 'ADMIN') {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['/users']);
          }
        }
      }, error: (error) => {
        this.errorMessages = error.message || 'Đăng nhập thất bại';
      }
    });
  }
}
