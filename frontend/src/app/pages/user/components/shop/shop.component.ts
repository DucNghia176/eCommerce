import {Component} from '@angular/core';
import {CommonModule, NgClass, NgForOf} from "@angular/common";
import {NzSliderComponent} from "ng-zorro-antd/slider";
import {FormsModule} from "@angular/forms";
import {NzInputNumberComponent} from "ng-zorro-antd/input-number";
import {ProductViewResponse} from "../../../../core/models/product.model";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faSearch} from "@fortawesome/free-solid-svg-icons";
import {ProductCardComponent} from "../../share/product-card/product-card.component";

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
  ],
  templateUrl: './shop.component.html',
  styleUrl: './shop.component.scss'
})
export class ShopComponent {
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

  products: ProductViewResponse[] = [
    {
      id: 1,
      name: "Điện thoại Galaxy S23",
      description: "Smartphone cao cấp Samsung Galaxy S23 với màn hình Dynamic AMOLED 6.1 inch.",
      price: "23000000",
      discountPrice: "21500000",
      categoryId: 101,
      categoryName: "Điện thoại",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 1, name: "Samsung"},
        {id: 2, name: "New"}
      ],
      unit: "cái",
      quantity: 50,
      skuCode: "S23-001",
      brand: {id: 1, name: "Samsung"},
      attributes: [
        {attribute: "Màu sắc", value: ["Đen", "Trắng", "Xanh"]},
        {attribute: "Bộ nhớ", value: ["128GB", "256GB"]}
      ],
      score: "4.7",
      user: 120,
      relatedProducts: [2, 3]
    },
    {
      id: 2,
      name: "Laptop Dell XPS 13",
      description: "Laptop Dell XPS 13 2025, i7 16GB RAM, 512GB SSD, màn hình FHD.",
      price: "35000000",
      discountPrice: "33000000",
      categoryId: 102,
      categoryName: "Laptop",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 3, name: "Dell"},
        {id: 4, name: "BestSeller"}
      ],
      unit: "cái",
      quantity: 30,
      skuCode: "XPS13-002",
      brand: {id: 2, name: "Dell"},
      attributes: [
        {attribute: "Màu sắc", value: ["Bạc", "Đen"]},
        {attribute: "RAM", value: ["16GB", "32GB"]}
      ],
      score: "4.5",
      user: 75,
      relatedProducts: [1, 3]
    },
    {
      id: 3,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 4,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 5,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 6,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 7,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 8,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 7,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 8,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 7,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 8,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 7,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 8,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 7,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 8,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 7,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }, {
      id: 8,
      name: "Tai nghe AirPods Pro",
      description: "Tai nghe không dây Apple AirPods Pro, chống ồn chủ động, sạc không dây.",
      price: "7000000",
      discountPrice: "6500000",
      categoryId: 103,
      categoryName: "Phụ kiện",
      thumbnailUrl: "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
      imageUrls: [
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg",
        "https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg"
      ],
      tags: [
        {id: 5, name: "Apple"},
        {id: 6, name: "Hot"}
      ],
      unit: "cái",
      quantity: 100,
      skuCode: "APRO-003",
      brand: {id: 3, name: "Apple"},
      attributes: [
        {attribute: "Màu sắc", value: ["Trắng"]}
      ],
      score: "4.8",
      user: 200,
      relatedProducts: [1, 2]
    }
  ];

  selectedCategoryId: number | null = null;
  priceRange: [number, number] = [0, 5000];

  selectedBrandIds: number[] = [];
  selectedSort = 'latest';
  protected readonly faSearch = faSearch;

  toggleCategory(cateId: number): void {
    this.selectedCategoryId = this.selectedCategoryId === cateId ? null : cateId;
  }

  tooltipFormatter(value: number): string {
    return value.toLocaleString() + '₫';
  }

  // Khi thay đổi giá trị min
  onMinInputChange(value: number) {
    if (value <= this.priceRange[1]) {
      this.priceRange = [value, this.priceRange[1]]; // update toàn bộ mảng
    } else {
      this.priceRange = [this.priceRange[1], this.priceRange[1]];
    }
  }

// Khi thay đổi giá trị max
  onMaxInputChange(value: number) {
    if (value >= this.priceRange[0]) {
      this.priceRange = [this.priceRange[0], value]; // update toàn bộ mảng
    } else {
      this.priceRange = [this.priceRange[0], this.priceRange[0]];
    }
  }

  toggleBrand(id: number) {
    if (this.selectedBrandIds.includes(id)) {
      // Bỏ chọn nếu đã chọn
      this.selectedBrandIds = this.selectedBrandIds.filter(b => b !== id);
    } else {
      // Thêm vào nếu chưa chọn
      this.selectedBrandIds.push(id);
    }
  }

  onSortChange() {
    console.log('Sort by:', this.selectedSort);
    // Ở đây bạn filter lại mảng products theo giá trị selectedSort
    // Ví dụ: nếu price-asc → sort theo giá tăng dần
  }
}
