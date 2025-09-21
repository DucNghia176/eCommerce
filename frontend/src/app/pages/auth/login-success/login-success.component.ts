import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-login-success',
  standalone: true,
  imports: [],
  templateUrl: './login-success.component.html',
  styleUrl: './login-success.component.scss'
})
export class LoginSuccessComponent implements OnInit {
  constructor(private router: Router) {
  }

  ngOnInit() {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    if (token) {
      localStorage.setItem('jwt', token);

      const payload = JSON.parse(atob(token.split('.')[1]));
      const roles: string[] = payload.role;

      if (roles.includes('ADMIN')) {
        this.router.navigate(['/admin']);
      } else if (roles.includes('USER')) {
        this.router.navigate(['/user']);
      } else {
        this.router.navigate(['/']); // fallback
      }
    } else {
      this.router.navigate(['/login']);
    }
  }
}
