import {Component, inject, OnInit} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faFacebook, faInstagram, faPinterest, faReddit, faTwitter, faYoutube} from "@fortawesome/free-brands-svg-icons";
import {
  faCartShopping,
  faChevronDown,
  faCircleInfo,
  faHeadphones,
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
import {NgIf} from "@angular/common";
import {UserResponse} from "../../../../core/models/user.model";
import {UsersService} from "../../../../core/services/users.service";
import {NzMenuModule} from "ng-zorro-antd/menu";
import {NzDropDownModule} from 'ng-zorro-antd/dropdown';
import {NzAvatarModule} from "ng-zorro-antd/avatar";
import {CartService} from "../../../../core/services/cart.service";
import {Cart} from "../../../../core/models/cart.model";
import {NzBadgeComponent} from "ng-zorro-antd/badge";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    FaIconComponent,
    RouterLink,
    AllCategoryComponent,
    NgIf,
    NzMenuModule,
    NzAvatarModule,
    NzDropDownModule,
    NzBadgeComponent,


  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  categories?: ChildCategoryResponse[];
  products?: FeaturedProductResponse[];
  isLoggedIn: boolean = false;
  user: UserResponse | null = null;
  cart: Cart | null = null;
  cartItemCount = 0;
  protected readonly faTwitter = faTwitter;
  protected readonly faFacebook = faFacebook;
  protected readonly faPinterest = faPinterest;
  protected readonly faYoutube = faYoutube;
  protected readonly faInstagram = faInstagram;
  protected readonly faReddit = faReddit;
  protected readonly faCartShopping = faCartShopping;
  protected readonly faUser = faUser;
  protected readonly search = faSearch;
  protected readonly faLocationDot = faLocationDot;
  protected readonly faRotate = faRotate;
  protected readonly faCircleInfo = faCircleInfo;
  protected readonly faPhoneVolume = faPhoneVolume;
  protected readonly faHeadphones = faHeadphones;
  protected readonly faChevronDown = faChevronDown;
  private authService = inject(AuthService);
  private router = inject(Router);
  private categoryService = inject(CategoryService);
  private authModal = inject(AuthModalService);
  private userService = inject(UsersService);
  private cartService = inject(CartService);

  ngOnInit(): void {
    this.authService.isAuthenticated$.subscribe(status => {
      this.isLoggedIn = status;
      if (status) {
        this.userService.getUserById().subscribe({
          next: (users) => {
            this.user = users;
          }
        });
        this.cartService.getCart();
      }
    });
    this.cartService.cart$.subscribe({
      next: (cart) => {
        this.cartItemCount = cart?.items?.length || 0;
      }
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
      this.router.navigate(['/user/shopCard']);
    }
  }

  onLogout() {
    this.authService.logout().subscribe({
      next: res => {
        console.log(res.message);
        this.user = null;
        window.location.href = '/user';
      },
      error: err => console.error(err)
    });
  }

  goToProfile() {
    this.router.navigate(['/user/profile']);
  }

  goToDashboard() {
    this.router.navigate(['/user/dashboard']);
  }

  private loadCategories() {
    this.categoryService.getAllWithFeature()
      .subscribe({
        next: data => {
          this.categories = data;
          // this.products = data;
        }, error: () => {
          this.categories = [];
        }
      })
  }
}
