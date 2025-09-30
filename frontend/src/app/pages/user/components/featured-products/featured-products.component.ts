import {Component} from '@angular/core';
import {CurrencyPipe, NgClass, NgForOf, SlicePipe} from "@angular/common";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faArrowRight, faCartPlus, faHeart} from "@fortawesome/free-solid-svg-icons";
import {FormsModule} from "@angular/forms";
import {NzRateModule} from 'ng-zorro-antd/rate';
import {NzModalService} from "ng-zorro-antd/modal";
import {faEye} from "@fortawesome/free-solid-svg-icons/faEye";
import {Router} from "@angular/router";
import {ProductDetailComponent} from "../product-detail/product-detail.component";

export interface Category {
  id: number;
  name: string;
  product: Product[];
}

export interface Product {
  id: number;
  name: string;
  rating: number;
  price: number;
  userView: number;
  imageUrl: string[];
}

@Component({
  selector: 'app-featured-products',
  standalone: true,
  imports: [
    NgForOf,
    SlicePipe,
    NgClass,
    FaIconComponent,
    CurrencyPipe,
    FormsModule,
    NzRateModule
  ],
  providers: [NzModalService],
  templateUrl: './featured-products.component.html',
  styleUrl: './featured-products.component.scss'
})
export class FeaturedProductsComponent {
  categories: Category[] = [
    {
      id: 1,
      name: 'Smartphones',
      product: [
        {
          id: 1,
          name: 'iPhone 15 Pro Max',
          rating: 4,
          price: 1199,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg', 'https://picsum.photos/200?random=21', 'https://picsum.photos/200?random=20', 'https://picsum.photos/200?random=1',]
        },
        {
          id: 2,
          name: 'Samsung Galaxy S23 Ultra',
          rating: 2,
          price: 1099, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 3,
          name: 'Google Pixel 8 Pro',
          rating: 1,
          price: 999, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 4,
          name: 'Xiaomi 13 Ultra',
          rating: 4.5,
          price: 899, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 5,
          name: 'OnePlus 11',
          rating: 4.4,
          price: 799, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
      ],
    },
    {
      id: 2,
      name: 'Laptops',
      product: [
        {
          id: 6,
          name: 'MacBook Pro 14"',
          rating: 4.9,
          price: 1999, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 7,
          name: 'Dell XPS 13',
          rating: 4.7,
          price: 1499, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 8,
          name: 'Asus ROG Strix G16',
          rating: 4.8,
          price: 1799, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 9,
          name: 'HP Spectre x360',
          rating: 4.6,
          price: 1399, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 10,
          name: 'Lenovo ThinkPad X1 Carbon',
          rating: 4.7,
          price: 1699, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 11,
          name: 'Acer Swift 5',
          rating: 4.4,
          price: 1099, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
      ],
    },
    {
      id: 3,
      name: 'Wearables',
      product: [
        {
          id: 12,
          name: 'Apple Watch Ultra 2',
          rating: 4.9,
          price: 799, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 13,
          name: 'Samsung Galaxy Watch 6',
          rating: 4.6,
          price: 349, userView: 1213,

          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 14,
          name: 'Fitbit Sense 2',
          rating: 4.3,
          price: 299,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 15,
          name: 'Garmin Fenix 7',
          rating: 4.7,
          price: 899,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 16,
          name: 'Xiaomi Mi Band 8',
          rating: 4.2,
          price: 59,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
      ],
    },
    {
      id: 4,
      name: 'Gaming & Accessories',
      product: [
        {
          id: 17,
          name: 'Sony PlayStation 5',
          rating: 4.9,
          price: 499,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 18,
          name: 'Xbox Series X',
          rating: 4.8,
          price: 499,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 19,
          name: 'Nintendo Switch OLED',
          rating: 4.7,
          price: 349,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 20,
          name: 'Logitech MX Master 3S',
          rating: 4.6,
          price: 99,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 21,
          name: 'Razer BlackWidow Keyboard',
          rating: 4.5,
          price: 149,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 22,
          name: 'Sony WH-1000XM5 Headphones',
          rating: 4.6,
          price: 399,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 23,
          name: 'HyperX Cloud Alpha Headset',
          rating: 4.4,
          price: 129,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
      ],
    },
    {
      id: 5,
      name: 'Tablets',
      product: [
        {
          id: 21, name: 'iPad Pro 12.9', rating: 4.9, price: 1099, userView: 1213,
          imageUrl: ['https://picsum.photos/200?random=21',
            'https://picsum.photos/200?random=22',
          ]
        },
        {
          id: 22,
          name: 'Samsung Galaxy Tab S9 Ultra',
          rating: 4.8,
          price: 999,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 23,
          name: 'Xiaomi Pad 6 Pro',
          rating: 4.6,
          price: 499,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 24,
          name: 'Amazon Fire HD 10',
          rating: 4.3,
          price: 149,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 25,
          name: 'Lenovo Tab P12 Pro',
          rating: 4.5,
          price: 699,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
      ],
    }, {
      id: 6,
      name: 'Smartwatches',
      product: [
        {
          id: 21,
          name: 'Apple Watch Ultra 2',
          rating: 4.9,
          price: 1099, userView: 1213,
          imageUrl: ['https://picsum.photos/200?random=21',
            'https://picsum.photos/200?random=22',]
        },
        {
          id: 22,
          name: 'SApple Watch Ultra 2',
          rating: 4.8,
          price: 999,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg', 'https://picsum.photos/200?random=21', 'https://picsum.photos/200?random=21',]
        },
        {
          id: 23,
          name: 'Xiaomi Pad 6 Pro',
          rating: 4.6,
          price: 499,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 24,
          name: 'Amazon Fire HD 10',
          rating: 4.3,
          price: 149,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
        {
          id: 25,
          name: 'Lenovo Tab P12 Pro',
          rating: 4.5,
          price: 699,
          userView: 1213,
          imageUrl: ['https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg']
        },
      ],
    },
  ];
  selectedCategory: string = 'All Product'; // mặc định chọn All Product
  isLoggedIn = false; // giả sử lấy từ auth service
  protected readonly faArrowRight = faArrowRight;
  protected readonly faHeart = faHeart;
  protected readonly faCartPlus = faCartPlus;
  protected readonly faEye = faEye;

  constructor(private modal: NzModalService, private router: Router) {
  }

  get displayedProducts(): Product[] {
    if (this.selectedCategory === 'All Product') {
      return this.categories.flatMap(c => c.product);
    }
    const category = this.categories.find(c => c.name === this.selectedCategory);
    return category ? category.product : [];
  }

  viewDetail(product: Product) {
    const modalRef = this.modal.create({
      nzTitle: product.name,
      nzContent: ProductDetailComponent,
      nzFooter: null,
      nzWidth: '80%',
      nzWrapClassName: 'custom-modal'
    });
    modalRef.getContentComponent().product = product;
  }


  selectCategory(name: string) {
    this.selectedCategory = name;
  }

  addToCart(product: any) {
    if (!this.isLoggedIn) {
      this.openLoginModal();
      return;
    }
    // logic thêm vào cart
    console.log('Add to cart', product);
  }

  addToWishlist(product: any) {
    if (!this.isLoggedIn) {
      this.openLoginModal();
      return;
    }
    // logic thêm vào wishlist
    console.log('Add to wishlist', product);
  }

  openLoginModal() {
    this.modal.create({
      nzTitle: 'Please login or register',
      nzContent: 'Bạn cần đăng nhập để thực hiện thao tác này.',
      nzFooter: [
        {
          label: 'Login',
          onClick: () => {
            this.router.navigate(['/auth/login']);
          }
        },
        {
          label: 'Register',
          onClick: () => {
            this.router.navigate(['/auth/register']);
          }
        }
      ]
    });
  }
}
