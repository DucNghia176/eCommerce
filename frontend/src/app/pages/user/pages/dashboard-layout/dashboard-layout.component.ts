import {Component} from '@angular/core';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from "@angular/router";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    NgForOf
  ],
  templateUrl: './dashboard-layout.component.html',
  styleUrl: './dashboard-layout.component.scss'
})
export class DashboardLayoutComponent {
  isSidebarOpen = true;

  menuItems = [
    {path: '/user/dashboard', label: 'Tổng quan', icon: '📊', active: false},
    {path: '/user/dashboard/orders', label: 'Đơn hàng', icon: '🛒', active: false},
    {path: '/user/dashboard/shipping', label: 'Giao hàng', icon: '🧱', active: false},
    {path: '/user/dashboard/setting', label: 'Cài đặt', icon: '⚙️', active: false},
  ];

  constructor(private router: Router) {
  }

  toggleSidebar() {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  logout() {
    if (confirm('Bạn có chắc muốn đăng xuất?')) {
      console.log('Đăng xuất');
      this.router.navigate(['/login']);
    }
  }
}
