import {Routes} from '@angular/router';
import {HomeComponent} from "./pages/home/home.component";
import {UserLayoutComponent} from "./user-layout/user-layout.component";
import {ShopSearchComponent} from "./pages/shop-search/shop-search.component";
import {ProductDetailComponent} from "./pages/product-detail/product-detail.component";
import {ShoppingCartComponent} from "./pages/shopping-cart/shopping-cart.component";
import {authGuard} from "../../guard/auth.guard";

export const userRoutes: Routes = [{
  path: '',
  component: UserLayoutComponent,
  children: [
    {path: '', component: HomeComponent, data: {breadcrumb: 'Trang chủ'}},
    {path: 'shop', component: ShopSearchComponent, data: {breadcrumb: 'Cửa hàng'}},
    {path: 'shop/:id', component: ProductDetailComponent, data: {breadcrumb: 'Chi tiết sản phẩm'}},
    {path: 'shopCard', component: ShoppingCartComponent, data: {breadcrumb: 'Giỏ hàng'}, canActivate: [authGuard]},
    {path: '**', redirectTo: '', pathMatch: 'full'}
  ]
}];
