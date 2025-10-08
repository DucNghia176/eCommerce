import {Component} from '@angular/core';
import {ShopComponent} from "../../components/shop/shop.component";

@Component({
  selector: 'app-shop-search',
  standalone: true,
  imports: [
    ShopComponent
  ],
  templateUrl: './shop-search.component.html',
  styleUrl: './shop-search.component.scss'
})
export class ShopSearchComponent {

}
