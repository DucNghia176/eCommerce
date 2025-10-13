import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router, RouterLink} from "@angular/router";
import {faHome} from "@fortawesome/free-solid-svg-icons";
import {filter} from "rxjs";
import {NzBreadCrumbItemComponent, NzBreadCrumbModule} from "ng-zorro-antd/breadcrumb";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-breadcrumb',
  standalone: true,
  imports: [
    RouterLink,
    NzBreadCrumbItemComponent,
    FaIconComponent,
    NzBreadCrumbModule,
    CommonModule,
  ],
  templateUrl: './breadcrumb.component.html',
  styleUrl: './breadcrumb.component.scss'
})
export class BreadcrumbComponent implements OnInit {
  breadcrumbs: { label: string; url: string }[] = [];
  protected readonly faHome = faHome;

  constructor(private router: Router) {
  }


  ngOnInit(): void {
    this.createBreadcrumbs(this.router.url);

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.createBreadcrumbs(this.router.url);
      });
  }

  createBreadcrumbs(url: string): void {
    // Bỏ phần query string (nếu có)
    const cleanUrl = url.split('?')[0];

    // Cắt url thành mảng các segment, loại bỏ rỗng
    const segments = cleanUrl.split('/').filter(x => x);

    let accumulatedUrl = '';
    this.breadcrumbs = segments.map(segment => {
      accumulatedUrl += '/' + segment;

      // Nếu là "user" thì không thêm vào breadcrumb
      if (segment.toLowerCase() === 'user') {
        return null;
      }

      return {
        label: this.formatLabel(segment),
        url: accumulatedUrl
      };
    }).filter(Boolean) as { label: string; url: string }[];
  }


  // Cắt chữ cho đẹp (viết hoa chữ cái đầu)
  formatLabel(segment: string): string {
    return decodeURIComponent(segment.charAt(0).toUpperCase() + segment.slice(1));
  }
}
