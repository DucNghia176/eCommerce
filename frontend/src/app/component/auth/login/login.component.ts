import {Component} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {Router} from "@angular/router";
import {AuthService} from "../../../services/auth.service";

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

  constructor(private authService: AuthService, private router: Router) {
  }

  onSubmit() {
    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        if (response.data?.token) {

          const token = response.data.token;
          this.authService.setToken(token);

          const payload = JSON.parse(atob(token.split('.')[1]));
          const role = payload.role;

          if (role === 'ADMIN') {
            this.router.navigate(['/users']);
          } else {
            this.router.navigate(['/home']);
          }
        }
      }, error: (error) => {
        this.errorMessages = error.message || 'Đăng nhập thất bại';
      }
    });
  }
}
