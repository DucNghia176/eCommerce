import {Component, ElementRef, ViewChild} from '@angular/core';
import {FormsModule, NgForm} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {Router, RouterModule} from "@angular/router";
import {AuthService} from "../../../core/services/auth.service";
import {AuthRequest} from "../../../core/models/auth.model";
import {validateAndFocusFirstError} from "../../../shared/utils/validation";
import {finalize} from "rxjs";
import {LoadingSpinnerComponent} from "../../../shared/components/loading-spinner/loading-spinner.component";
import {Role} from "../../../shared/status/role";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, LoadingSpinnerComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessages: string | null = null;
  isLoading = false;
  @ViewChild('formRef') formRef!: ElementRef;
  showPassword: boolean = false;

  constructor(private authService: AuthService, private router: Router) {
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  login(form: NgForm) {
    if (!validateAndFocusFirstError(form, this.formRef)) return;

    const auth: AuthRequest = {
      usernameOrEmail: this.username,
      password: this.password
    };

    this.isLoading = true;
    this.authService.login(auth)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (response) => {
          if (response.data?.token) {

            const token = response.data.token;
            this.authService.setToken(token);

            const payload = JSON.parse(atob(token.split('.')[1]));
            const roles: string[] = payload.role;

            if (roles.includes(Role.admin)) {
              this.router.navigate(['/admin']);
            } else if (roles.includes(Role.user)) {
              this.router.navigate(['/user']);
            } else {
              this.router.navigate(['/']); // fallback
            }
          }
        }, error: (error) => {
          this.errorMessages = error.message || 'Đăng nhập thất bại';
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
