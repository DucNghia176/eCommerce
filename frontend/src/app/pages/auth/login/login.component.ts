import {Component, ElementRef, ViewChild} from '@angular/core';
import {FormsModule, NgForm} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {Router, RouterModule} from "@angular/router";
import {AuthService} from "../../../core/services/auth.service";
import {AuthRequest} from "../../../core/models/auth.model";
import {validateAndFocusFirstError} from "../../../shared/utils/validation";
import {Role} from "../../../shared/status/role";
import {finalize} from "rxjs";
import {LoadingSpinnerComponent} from "../../../shared/components/loading-spinner/loading-spinner.component";

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

  constructor(private authService: AuthService, private router: Router) {
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
            const role = payload.role;

            if (role === Role.admin) {
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

  loginWithGoogle() {
    window.location.href = 'http://localhost:8085/oauth2/authorization/google';
  }
}
