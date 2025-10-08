import {Component, model, OnInit} from '@angular/core';
import {NgClass, NgForOf, SlicePipe} from "@angular/common";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faArrowRight, faCartPlus, faHeart} from "@fortawesome/free-solid-svg-icons";
import {FormsModule} from "@angular/forms";
import {NzRateModule} from 'ng-zorro-antd/rate';
import {NzModalService} from "ng-zorro-antd/modal";
import {faEye} from "@fortawesome/free-solid-svg-icons/faEye";
import {Router} from "@angular/router";
import {ProductByTagResponse} from "../../../../core/models/product.model";
import {ProductService} from "../../../../core/services/product.service";
import {ProductCardComponent} from "../../share/product-card/product-card.component";

export interface Category {
  id: number;
  name: string;
  product: ProductByTagResponse[];
}

@Component({
  selector: 'app-featured-products',
  standalone: true,
  imports: [
    NgForOf,
    SlicePipe,
    NgClass,
    FaIconComponent,
    FormsModule,
    NzRateModule,
    ProductCardComponent
  ],
  providers: [NzModalService],
  templateUrl: './featured-products.component.html',
  styleUrl: './featured-products.component.scss'
})
export class FeaturedProductsComponent implements OnInit {
  products: ProductByTagResponse[] = [];
  categories: Category[] = [];
  selectedCategory: number = -1;
  isLoggedIn = false; // giả sử lấy từ auth service
  protected readonly faArrowRight = faArrowRight;
  protected readonly faHeart = faHeart;
  protected readonly faCartPlus = faCartPlus;
  protected readonly faEye = faEye;
  protected readonly model = model;

  constructor(private modal: NzModalService, private router: Router, private productService: ProductService) {
  }

  get displayedProducts(): ProductByTagResponse[] {
    if (this.selectedCategory === -1) { // All Product
      return this.products;
    }
    const category = this.categories.find(c => c.id === this.selectedCategory);
    return category ? category.product : [];
  }

  ngOnInit() {
    this.loadProductByTag(['FEATURE']);
  }

  loadProductByTag(tags?: string[]) {
    this.productService.productByTag({tags, page: 0, size: 100, sort: 'price,desc'}).subscribe(pageData => {
      this.products = pageData.content;

      // Group theo categoryId
      const categoryMap = this.products.reduce((map, p) => {
        if (!map[p.categoryId!]) {
          map[p.categoryId!] = {id: p.categoryId!, name: p.categoryName!, product: []};
        }
        map[p.categoryId!].product.push(p);
        return map;
      }, {} as Record<number, Category>);

      this.categories = Object.values(categoryMap);
    });
  }

  selectCategory(id: number) {
    this.selectedCategory = id;
  }
}
