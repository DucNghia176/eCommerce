import {Routes} from '@angular/router';
import {UserAdminComponent} from "./pages/user/user.component";
import {DashboardComponent} from "./pages/dashboard/dashboard.component";
import {OrdersComponent} from "./pages/orders/orders.component";
import {ProductsComponent} from "./pages/products/products.component";
import {CategoryComponent} from "./pages/category/category.component";
import {AdminLayoutComponent} from "./layout/admin-layout/admin-layout.component";
import {CreateProductComponent} from "./components/create-product/create-product.component";

export const adminRoutes: Routes = [{
  path: '',
  canActivate: [],
  component: AdminLayoutComponent,
  children: [
    {path: '', component: DashboardComponent},
    {path: 'users', component: UserAdminComponent},
    {path: 'orders', component: OrdersComponent},
    {path: 'product', component: ProductsComponent},
    {path: 'product/create', component: CreateProductComponent},
    {path: 'category', component: CategoryComponent},
    {path: '**', redirectTo: '', pathMatch: 'full'}
  ]
}];
