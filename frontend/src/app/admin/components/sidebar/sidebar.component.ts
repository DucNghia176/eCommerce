import {Component} from '@angular/core';
import {Router, RouterModule} from "@angular/router";
import {FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {
  faChartColumn,
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

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, FontAwesomeModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
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

  constructor(private router: Router) {
  }

  isActive(path: string): string {
    return this.router.url.startsWith(path) ?
      'bg-white text-[#0D1B45] font-semibold' : '';
  }
}
