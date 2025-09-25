import {Component} from '@angular/core';
import {HeaderComponent} from "../../components/header/header.component";
import {FooterComponent} from "../../components/footer/footer.component";
import {ProductHighlightsComponent} from "../../components/product-highlights/product-highlights.component";
import {ShopWithCategoryComponent} from "../../components/shop-with-category/shop-with-category.component";
import {FeaturedProductsComponent} from "../../components/featured-products/featured-products.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    HeaderComponent,
    FooterComponent,
    ProductHighlightsComponent,
    ShopWithCategoryComponent,
    FeaturedProductsComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

}
