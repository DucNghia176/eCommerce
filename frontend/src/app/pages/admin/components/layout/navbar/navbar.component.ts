import {Component, HostListener, OnInit} from '@angular/core';
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {faBell, faChevronDown, faCommentAlt, faSearch} from "@fortawesome/free-solid-svg-icons";
import {CommonModule, NgOptimizedImage} from "@angular/common";
import {Router} from "@angular/router";
import {UserResponse} from "../../../../../core/models/user.model";
import {UsersService} from "../../../../../core/services/users.service";
import {AuthService} from "../../../../../core/services/auth.service";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [FontAwesomeModule, CommonModule, NgOptimizedImage],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit {
  search = faSearch;
  message = faCommentAlt;
  notification = faBell;
  down = faChevronDown;
  dropdownOpen = false;
  user: UserResponse | null = null;

  constructor(private userService: UsersService, private authService: AuthService, private router: Router) {
  }


  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/user']);
  }

  ngOnInit() {
    this.userService.getUserById().subscribe({
      next: (users) => {
        this.user = users;
      },
      error: (err) => {
        console.error('Lỗi khi lấy user:', err);
      }
    });
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent) {
    const clickedInside = (event.target as HTMLElement).closest('.group');
    if (!clickedInside) {
      this.dropdownOpen = false;
    }
  }

}
