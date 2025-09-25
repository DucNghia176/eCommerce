import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router, RouterLink} from "@angular/router";
import {TitleCasePipe} from "@angular/common";
import {faHome} from "@fortawesome/free-solid-svg-icons";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";

@Component({
  selector: 'app-breadcrumb',
  standalone: true,
  imports: [
    RouterLink,
    TitleCasePipe,
    FaIconComponent
  ],
  templateUrl: './breadcrumb.component.html',
  styleUrl: './breadcrumb.component.scss'
})
export class BreadcrumbComponent implements OnInit {
  breadcrumbs: string[] = [];
  protected readonly faHome = faHome;

  constructor(private router: Router) {
  }

  ngOnInit(): void {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.buildBreadcrumb(event.urlAfterRedirects);
      }
    });
  }

  private buildBreadcrumb(url: string): void {
    // Cắt URL thành từng đoạn
    this.breadcrumbs = url
      .split('/')
      .filter(x => x); // bỏ phần rỗng
  }
}
