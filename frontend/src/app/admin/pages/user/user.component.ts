import {Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UsersService} from '../../../core/services/users.service';
import {UserResponse} from "../../../core/models/user.model";
import {AuthService} from "../../../core/services/auth.service";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {
  faAdd,
  faDeleteLeft,
  faFileExcel,
  faFileExport,
  faFileImport,
  faLock,
  faSearch,
  faTrash,
  faUnlock
} from "@fortawesome/free-solid-svg-icons";
import {FormsModule} from "@angular/forms";
import {PageComponent} from "../../../shared/components/page/page.component";
import {PageSize} from "../../../shared/status/page-size";

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FaIconComponent, FormsModule, PageComponent],
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss'
})
export class UserAdminComponent implements OnInit {
  users: UserResponse[] = [];
  errorMessage: string | null = null;
  email: string = '';
  totalPages = 0;
  pageSize: number = 10;
  totalItems: number = 0;
  activeCount: number = 0;
  inactiveCount: number = 0;
  currentPage = 0;
  isLock: number = -1;
  avatar?: File;
  pageSizes: number[] = PageSize;
  protected readonly faAdd = faAdd;
  protected readonly faSearch = faSearch;
  protected readonly faLock = faLock;
  protected readonly faUnlock = faUnlock;
  protected readonly Math = Math;
  protected readonly faFileImport = faFileImport;
  protected readonly faFileExport = faFileExport;
  protected readonly faFileExcel = faFileExcel;
  protected readonly faTrash = faTrash;
  protected readonly faDeleteLeft = faDeleteLeft;
  private userService = inject(UsersService);
  private authService = inject(AuthService);

  ngOnInit() {
    this.loadUser();
    this.countUsers();
  }

  toggleLock(id: string) {
    this.userService.toggleLock(id).subscribe({
      next: () => {
        this.loadUser();
        this.countUsers();
        this.errorMessage = null;
      },
      error: (error) => {
        this.errorMessage = error.message;
      }
    });
  }

  toggleRole(id: string) {
    this.userService.toggleRole(id).subscribe({
      next: () => {
        this.loadUser();
        this.countUsers();
        this.errorMessage = null;
      },
      error: (error) => {
        this.errorMessage = error.message;
      }
    });
  }

  public loadUser(page: number = 0): void {
    const lockParam = this.isLock === -1 ? undefined : this.isLock;
    this.userService.getAllUsers(page, this.pageSize, lockParam).subscribe({
      next: (data) => {
        const allUsers = data.content;
        this.currentPage = data.number;
        // this.totalItems = data.totalElements;
        this.totalPages = data.totalPages;

        this.users = allUsers;
        this.errorMessage = null;
      },
      error: (err) => {
        this.users = [];
        this.errorMessage = err.message;
      }
    });
  }

  onPageSizeChange(newSize: number) {
    this.pageSize = newSize;
    this.loadUser(0);
    this.countUsers();
  }

  onFilterChange(newIsLock: number): void {
    this.isLock = newIsLock;
    this.loadUser(0);
  }

  private countUsers(): void {
    this.userService.count().subscribe({
      next: (data) => {
        this.activeCount = data.active;
        this.inactiveCount = data.inactive;
        this.totalItems = data.all;
      },
      error: () => {
        this.activeCount = 0;
        this.inactiveCount = 0;
        this.totalItems = 0;
      }
    });
  }
}
