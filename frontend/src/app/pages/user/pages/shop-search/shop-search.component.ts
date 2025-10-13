import {Component, OnInit} from '@angular/core';
import {ShopComponent} from "../../components/shop/shop.component";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-shop-search',
  standalone: true,
  imports: [
    ShopComponent
  ],
  templateUrl: './shop-search.component.html',
  styleUrl: './shop-search.component.scss'
})
export class ShopSearchComponent implements OnInit {
  keyword: string = '';

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit() {
    // Lắng nghe sự thay đổi keyword từ query params
    this.route.queryParams.subscribe(params => {
      this.keyword = params['keyword'] || '';
    });
  }
}
