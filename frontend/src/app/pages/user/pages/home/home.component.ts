import {Component} from '@angular/core';
import {ProductHighlightsComponent} from "../../components/product-highlights/product-highlights.component";
import {ShopWithCategoryComponent} from "../../components/shop-with-category/shop-with-category.component";
import {FeaturedProductsComponent} from "../../components/featured-products/featured-products.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    ProductHighlightsComponent,
    ShopWithCategoryComponent,
    FeaturedProductsComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
}
