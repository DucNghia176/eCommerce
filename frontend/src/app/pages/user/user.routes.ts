import {Routes} from '@angular/router';
import {HomeComponent} from "./pages/home/home.component";
import {UserLayoutComponent} from "./user-layout/user-layout.component";
import {ShopSearchComponent} from "./pages/shop-search/shop-search.component";
import {ProductDetailComponent} from "./pages/product-detail/product-detail.component";

export const userRoutes: Routes = [{
  path: '',
  canActivate: [],
  component: UserLayoutComponent,
  children: [
    {path: '', component: HomeComponent},
    {path: 'shop', component: ShopSearchComponent},
    {path: 'shop/:id', component: ProductDetailComponent},
    {path: '**', redirectTo: '', pathMatch: 'full'}
  ]
}];
