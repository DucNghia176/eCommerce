import {Routes} from '@angular/router';
import {UserAdminComponent} from "./pages/user/user.component";
import {DashboardComponent} from "./pages/dashboard/dashboard.component";
import {OrdersComponent} from "./pages/orders/orders.component";
import {ProductsComponent} from "./pages/products/products.component";
import {CategoryComponent} from "./pages/category/category.component";
import {AdminLayoutComponent} from "./layout/admin-layout/admin-layout.component";
import {CreateProductComponent} from "./components/product/create-product/create-product.component";
import {UpdateProductComponent} from "./components/product/update-product/update-product.component";
import {CategoryCreateComponent} from "./components/category/category-create/category-create.component";
import {CategoryDetailComponent} from "./components/category/category-detail/category-detail.component";

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
    {path: 'product/update/:id', component: UpdateProductComponent},
    {path: 'category', component: CategoryComponent},
    {path: 'category/create', component: CategoryCreateComponent},
    {path: 'category/detail/:id', component: CategoryDetailComponent},
    {path: '**', redirectTo: '', pathMatch: 'full'}
  ]
}];
