import {Component, OnInit} from '@angular/core';
import {Router, RouterModule} from "@angular/router";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {
  faBars,
  faChartColumn,
  faChevronLeft,
  faChevronRight,
  faCog,
  faFileExport,
  faHome,
  faLightbulb,
  faMessage,
  faQuestionCircle,
  faShoppingCart,
  faTags,
  faTicketAlt,
  faUserCog,
  faUsers
} from "@fortawesome/free-solid-svg-icons";
import {CommonModule, NgClass} from "@angular/common";

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, FontAwesomeModule, NgClass, CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent implements OnInit {
  home = faHome
  orders = faShoppingCart
  product = faTags
  category = faFileExport
  user = faUsers
  reports = faChartColumn
  coupon = faTicketAlt
  inbox = faMessage
  know = faQuestionCircle
  productUpdate = faLightbulb
  personal = faUserCog
  global = faCog

  isCollapsed = false;
  protected readonly faBars = faBars;
  protected readonly faChevronRight = faChevronRight;
  protected readonly faChevronLeft = faChevronLeft;

  constructor(private router: Router) {
  }

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
    localStorage.setItem('sidebar-collapsed', JSON.stringify(this.isCollapsed));
  }

  ngOnInit() {
    const saved = localStorage.getItem('sidebar-collapsed');
    this.isCollapsed = saved ? JSON.parse(saved) : false;
  }
}
