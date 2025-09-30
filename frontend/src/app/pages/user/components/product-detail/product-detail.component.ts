import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Product} from "../featured-products/featured-products.component";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {CurrencyPipe, NgClass, NgForOf} from "@angular/common";
import {faArrowLeft, faArrowRight, faCartPlus, faMinus, faPlus} from "@fortawesome/free-solid-svg-icons";
import {NzRateComponent} from "ng-zorro-antd/rate";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    FaIconComponent,
    NgForOf,
    NgClass,
    NzRateComponent,
    FormsModule,
    CurrencyPipe
  ],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {
  @Input() product!: Product;
  @ViewChild('scrollContainer', {static: true}) scrollContainer!: ElementRef;
  selectedIndex: number = 0;
  quantity = 1;
  protected readonly faArrowRight = faArrowRight;
  protected readonly faArrowLeft = faArrowLeft;
  protected readonly faPlus = faPlus;
  protected readonly faMinus = faMinus;
  protected readonly faCartPlus = faCartPlus;

  get selectedImage(): string {
    return this.product.imageUrl[this.selectedIndex];
  }

  ngOnInit() {
    if (this.product.imageUrl && this.product.imageUrl.length) {
      this.selectedIndex = 0;
    }
  }

  selectImage(index: number) {
    this.selectedIndex = index;
  }

  scrollLeft() {
    const container = this.scrollContainer.nativeElement;
    const newPos = container.scrollLeft - 200;
    container.scrollTo({left: newPos, behavior: 'smooth'});
    if (this.selectedIndex > 0) {
      this.selectedIndex--;
    }
  }

  scrollRight() {
    const container = this.scrollContainer.nativeElement;
    const newPos = container.scrollLeft + 200;
    container.scrollTo({left: newPos, behavior: 'smooth'});
    if (this.selectedIndex < this.product.imageUrl.length - 1) {
      this.selectedIndex++;
    }
  }

  increase() {
    this.quantity++;
  }

  decrease() {
    if (this.quantity > 1) this.quantity--;
  }
}
