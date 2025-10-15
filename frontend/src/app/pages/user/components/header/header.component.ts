import {Component, inject, OnInit} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faFacebook, faInstagram, faPinterest, faReddit, faTwitter, faYoutube} from "@fortawesome/free-brands-svg-icons";
import {
  faCartShopping,
  faChevronDown,
  faCircleInfo,
  faHeadphones,
  faHeadset,
  faHeart,
  faLocationDot,
  faPhoneVolume,
  faRotate,
  faSearch,
  faUser
} from "@fortawesome/free-solid-svg-icons";
import {Router, RouterLink} from "@angular/router";
import {AllCategoryComponent} from "../all-category/all-category.component";
import {AuthService} from "../../../../core/services/auth.service";
import {ChildCategoryResponse, FeaturedProductResponse} from "../../../../core/models/category.model";
import {CategoryService} from "../../../../core/services/category.service";
import {AuthModalService} from "../../../../shared/service/auth-modal.service";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    FaIconComponent,
    RouterLink,
    AllCategoryComponent
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  categories?: ChildCategoryResponse[];
  products?: FeaturedProductResponse[];
  isLoggedIn?: boolean;
  protected readonly faTwitter = faTwitter;
  protected readonly faFacebook = faFacebook;
  protected readonly faPinterest = faPinterest;
  protected readonly faYoutube = faYoutube;
  protected readonly faInstagram = faInstagram;
  protected readonly faReddit = faReddit;
  protected readonly faCartShopping = faCartShopping;
  protected readonly faHeart = faHeart;
  protected readonly faUser = faUser;
  protected readonly search = faSearch;
  protected readonly faLocationDot = faLocationDot;
  protected readonly faRotate = faRotate;
  protected readonly faHeadset = faHeadset;
  protected readonly faCircleInfo = faCircleInfo;
  protected readonly faPhoneVolume = faPhoneVolume;
  protected readonly faHeadphones = faHeadphones;
  protected readonly faChevronDown = faChevronDown;
  private authService = inject(AuthService);
  private router = inject(Router);
  private categoryService = inject(CategoryService);
  private authModal = inject(AuthModalService);

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }

  ngOnInit(): void {
    this.authService.isAuthenticated$.subscribe(status => {
      this.isLoggedIn = status;
    });

    this.loadCategories();
  }

  onSearchInput(event: any) {
    const keyword = event.target.value.trim();

    if (keyword.length > 0) {
      // Điều hướng sang trang tìm kiếm, truyền keyword qua query params
      this.router.navigate(['/user/shop'], {queryParams: {keyword}});
    }
  }

  onCartClick(): void {
    if (!this.isLoggedIn) {
      this.authModal.openModal();
    } else {
      this.router.navigate(['/user/cart']);
    }
  }

  onWishlistClick(): void {
    if (!this.isLoggedIn) {
      this.authModal.openModal();
    } else {
      this.router.navigate(['/user/wishlist']);
    }
  }

  onProfileClick(): void {
    if (!this.isLoggedIn) {
      this.authModal.openModal();
    } else {
      this.router.navigate(['/user/profile']);
    }
  }

  private loadCategories() {
    this.categoryService.getAllWithFeature()
      .subscribe({
        next: data => {
          this.categories = data;
          // this.products = data;
        }, error: err => {
          this.categories = [];
        }
      })
  }

}
