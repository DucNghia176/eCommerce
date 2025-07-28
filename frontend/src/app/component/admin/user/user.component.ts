import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UsersService} from '../../../services/users.service';
import {UserResponse} from "../../../models/user.model";
import {AuthService} from "../../../services/auth.service";

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss'
})
export class UserComponent {
  users: UserResponse[] = [];
  errorMessage: string | null = null;
  private userService = inject(UsersService);

  constructor(private authService: AuthService) {
  }

  ngOnInit() {
    const token = this.authService.getToken();
    console.log('Token ở user.component:', token);

    this.authService.token$.subscribe(token => {
      console.log('Token thay đổi:', token);
    });

    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.errorMessage = null;
      },
      error: (error) => {
        this.errorMessage = error.message;
        this.users = [];
      }
    });
  }

}
