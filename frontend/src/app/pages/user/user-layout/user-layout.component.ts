import {Component} from '@angular/core';
import {BreadcrumbComponent} from "../../../shared/components/breadcrumb/breadcrumb.component";
import {FooterComponent} from "../components/footer/footer.component";
import {HeaderComponent} from "../components/header/header.component";
import {NavigationEnd, Router, RouterOutlet} from "@angular/router";
import {filter} from "rxjs";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [
    BreadcrumbComponent,
    FooterComponent,
    HeaderComponent,
    RouterOutlet,
    CommonModule
  ],
  templateUrl: './user-layout.component.html',
  styleUrl: './user-layout.component.scss'
})
export class UserLayoutComponent {
  currentUrl = '';

  constructor(public router: Router) {
    // Lắng nghe sự kiện thay đổi URL
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(event => {
        const navEndEvent = event as NavigationEnd;
        this.currentUrl = navEndEvent.urlAfterRedirects;
      });
  }
}
