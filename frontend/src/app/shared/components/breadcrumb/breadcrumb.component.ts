import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router, RouterLink} from "@angular/router";
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

  constructor(private router: Router, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    // Lắng nghe khi điều hướng thay đổi
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.breadcrumbs = this.buildBreadCrumb(this.route.root);
      });

    // Khởi tạo lần đầu
    this.breadcrumbs = this.buildBreadCrumb(this.route.root);
  }

  /**
   * Đệ quy duyệt qua các route con để xây breadcrumb
   */
  buildBreadCrumb(
    route: ActivatedRoute,
    url: string = '',
    breadcrumbs: { label: string; url: string }[] = []
  ): { label: string; url: string }[] {
    const children = route.children;

    if (children.length === 0) {
      return breadcrumbs;
    }

    for (const child of children) {
      const routeURL = child.snapshot.url.map(segment => segment.path).join('/');
      if (routeURL !== '') {
        url += `/${routeURL}`;
      }

      const label = child.snapshot.data['breadcrumb'];
      if (label) {
        breadcrumbs.push({label, url});
      }

      // Tiếp tục đệ quy (không return sớm)
      this.buildBreadCrumb(child, url, breadcrumbs);
    }

    return breadcrumbs;
  }
}
