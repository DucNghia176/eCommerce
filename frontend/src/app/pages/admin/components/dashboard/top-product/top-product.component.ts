import {Component, Input} from '@angular/core';
import {CommonModule, NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-top-product',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage],
  templateUrl: './top-product.component.html',
  styleUrl: './top-product.component.scss'
})
export class TopProductComponent {
  @Input() products: {
    name: string;
    price: string;
    units: number;
    image: string;
  }[] = [];
}
