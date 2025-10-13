import {Component, inject, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {CommonModule, NgClass, NgForOf} from "@angular/common";
import {NzSliderComponent} from "ng-zorro-antd/slider";
import {FormsModule} from "@angular/forms";
import {NzInputNumberComponent} from "ng-zorro-antd/input-number";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faSearch} from "@fortawesome/free-solid-svg-icons";
import {ProductCardComponent} from "../../share/product-card/product-card.component";
import {SearchProductResponse} from "../../../../core/models/product.model";
import {ProductService} from "../../../../core/services/product.service";
import {debounceTime, distinctUntilChanged, Subject} from "rxjs";
import {NzRateComponent} from "ng-zorro-antd/rate";

export interface Category {
  id: number;
  name: string;
}

export interface Brand {
  id: number;
  name: string;
}

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [
    NgForOf,
    NgClass,
    NzSliderComponent,
    FormsModule,
    NzInputNumberComponent,
    FaIconComponent,
    ProductCardComponent,
    CommonModule,
    NzRateComponent,
  ],
  templateUrl: './shop.component.html',
  styleUrl: './shop.component.scss'
})
export class ShopComponent implements OnInit, OnChanges {
  categories: Category[] = [
    {id: 1, name: 'Điện thoại'},
    {id: 2, name: 'Laptop'},
    {id: 3, name: 'Máy ảnh'},
    {id: 4, name: 'Tai nghe'},
    {id: 5, name: 'Đồng hồ'},
  ];
  brands: Brand[] = [
    {id: 1, name: 'Apple'},
    {id: 2, name: 'Samsung'},
    {id: 3, name: 'Sony'},
    {id: 4, name: 'Xiaomi'},
    {id: 5, name: 'Apple'},
    {id: 6, name: 'Samsung'},
    {id: 7, name: 'Sony'},
    {id: 8, name: 'Xiaomi'},
    {id: 9, name: 'Apple'},
    {id: 10, name: 'Samsung'},
    {id: 11, name: 'Sony'},
    {id: 12, name: 'Xiaomi'},
  ];
  products: SearchProductResponse[] = [
    {
      id: 1,
      name: "Product 1",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "120000",
      discountPrice: "100000",
      score: "4.5",
      user: 101
    },
    {
      id: 2,
      name: "Product 2",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "250000",
      discountPrice: "220000",
      score: "4.0",
      user: 102
    },
    {
      id: 3,
      name: "Product 3",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "50000",
      discountPrice: "45000",
      score: "3.8",
      user: 103
    },
    {
      id: 4,
      name: "Product 4",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "300000",
      discountPrice: "280000",
      score: "4.7",
      user: 104
    },
    {
      id: 5,
      name: "Product 5",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "150000",
      discountPrice: "130000",
      score: "4.2",
      user: 105
    },
    {
      id: 6,
      name: "Product 6",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "80000",
      discountPrice: "75000",
      score: "3.5",
      user: 106
    },
    {
      id: 7,
      name: "Product 7",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "180000",
      discountPrice: "160000",
      score: "4.6",
      user: 107
    },
    {
      id: 8,
      name: "Product 8",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "220000",
      discountPrice: "200000",
      score: "4.1",
      user: 108
    },
    {
      id: 9,
      name: "Product 9",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "90000",
      discountPrice: "85000",
      score: "3.9",
      user: 109
    },
    {
      id: 10,
      name: "Product 10",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "270000",
      discountPrice: "250000",
      score: "4.8",
      user: 110
    },
    {
      id: 11,
      name: "Product 11",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "140000",
      discountPrice: "120000",
      score: "4.3",
      user: 111
    },
    {
      id: 12,
      name: "Product 12",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "100000",
      discountPrice: "95000",
      score: "3.7",
      user: 112
    },
    {
      id: 13,
      name: "Product 13",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "320000",
      discountPrice: "300000",
      score: "4.9",
      user: 113
    },
    {
      id: 14,
      name: "Product 14",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "60000",
      discountPrice: "55000",
      score: "3.6",
      user: 114
    },
    {
      id: 15,
      name: "Product 15",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "400000",
      discountPrice: "380000",
      score: "4.4",
      user: 115
    },
    {
      id: 16,
      name: "Product 16",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "75000",
      discountPrice: "70000",
      score: "3.8",
      user: 116
    },
    {
      id: 17,
      name: "Product 17",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "200000",
      discountPrice: "180000",
      score: "4.0",
      user: 117
    },
    {
      id: 18,
      name: "Product 18",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "95000",
      discountPrice: "90000",
      score: "3.9",
      user: 118
    },
    {
      id: 19,
      name: "Product 19",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "260000",
      discountPrice: "240000",
      score: "4.5",
      user: 119
    },
    {
      id: 20,
      name: "Product 20",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      price: "180000",
      discountPrice: "160000",
      score: "4.2",
      user: 120
    },
  ];
  @Input() keyword ?: string;
  page: number = 0;
  pageSize: number = 20;
  total: number = 0;
  sortOrder: 'asc' | 'desc' = 'desc';
  ratingFrom?: number;
  selectedCategoryId?: number;
  priceRange!: [number, number];
  selectedBrandIds: number[] = [];
  selectedSort = '';
  protected readonly faSearch = faSearch;
  private searchSubject = new Subject<string>();
  private productService = inject(ProductService);

  ngOnChanges(changes: SimpleChanges) {
    if (changes['keyword'] && !changes['keyword'].firstChange) {
      this.search();
    }
  }

  ngOnInit() {
    if (this.keyword) {
      this.search();
    }

    this.searchSubject.pipe(
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(keyword => {
      this.keyword = keyword;
      this.search();
    });

    this.search();
  }

  onSearchInput(event: any) {
    const value = event.target.value;
    this.searchSubject.next(value);
  }

  toggleCategory(id: number): void {
    this.selectedCategoryId = this.selectedCategoryId === id ? undefined : id;

    this.search();
  }

  tooltipFormatter(value: number): string {
    return value.toLocaleString() + '₫';
  }

  onMinInputChange(value: number) {
    if (!this.priceRange) return;
    const max = this.priceRange[1];
    this.priceRange = [Math.min(value, max), max];
    this.search();
  }

// Khi thay đổi giá trị max
  onMaxInputChange(value: number) {
    if (!this.priceRange) return;
    const min = this.priceRange[0];
    this.priceRange = [min, Math.max(value, min)];
    this.search();
  }

  toggleBrand(id: number) {
    if (this.selectedBrandIds.includes(id)) {
      // Bỏ chọn nếu đã chọn
      this.selectedBrandIds = this.selectedBrandIds.filter(b => b !== id);
    } else {
      // Thêm vào nếu chưa chọn
      this.selectedBrandIds.push(id);
    }
    this.search();
  }


  onSortChange(event: Event) {
    const target = event.target as HTMLSelectElement;
    this.selectedSort = target.value;
    this.sortOrder = 'desc';
    this.search();
  }

  toggleSortOrder() {
    this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
    this.search();
  }


  search(page: number = 0) {
    console.log(`Sorting by ${this.selectedSort} in ${this.sortOrder} order`);

    const params = {
      keyword: this.keyword,
      categoryId: this.selectedCategoryId ?? undefined,
      brandId: this.selectedBrandIds.length > 0 ? this.selectedBrandIds : undefined,
      priceFrom: this.priceRange?.[0],
      priceTo: this.priceRange?.[1],
      ratingFrom: this.ratingFrom,
      page,
      size: this.pageSize,
      sort: `${this.selectedSort},${this.sortOrder}`
    }
    this.productService.searchProduct(params).subscribe({
      next: data => {
        this.products = data.content;
        this.page = data.totalPages;
        this.pageSize = data.size;
        this.total = data.totalElements;
      },
      error: error => console.log(error)
    })
  }
}
