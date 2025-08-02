import {Component} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {Router} from "@angular/router";
import {AuthService} from "../../core/services/auth.service";

@Component({
  selector: 'app-confirm-email',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './confirm-email.component.html',
  styleUrl: './confirm-email.component.scss'
})
export class ConfirmEmailComponent {
  code: string = '';

  constructor(authService: AuthService, private router: Router) {
  }
}
