import {Routes} from '@angular/router';
import {HomeComponent} from "./pages/home/home.component";
import {UserLayoutComponent} from "./user-layout/user-layout.component";
import {ShopSearchComponent} from "./pages/shop-search/shop-search.component";
import {ProductDetailComponent} from "./pages/product-detail/product-detail.component";
import {ShoppingCartComponent} from "./pages/shopping-cart/shopping-cart.component";
import {authGuard} from "../../guard/auth.guard";
import {DashboardLayoutComponent} from "./pages/dashboard-layout/dashboard-layout.component";
import {DashboardComponent} from "./components/dashboard/dashboard.component";
import {OrdersComponent} from "./components/orders/orders.component";
import {SettingsComponent} from "./components/settings/settings.component";
import {ShippingComponent} from "./components/shipping/shipping.component";
import {PaymentSuccessComponent} from "./components/payment-success/payment-success.component";
import {PaymentCancelComponent} from "./components/payment-cancel/payment-cancel.component";

export const userRoutes: Routes = [{
  path: '',
  component: UserLayoutComponent,
  children: [
    {path: '', component: HomeComponent, data: {breadcrumb: 'Trang chủ'}},
    {path: 'shop', component: ShopSearchComponent, data: {breadcrumb: 'Cửa hàng'}},
    {path: 'shop/:id', component: ProductDetailComponent, data: {breadcrumb: 'Chi tiết sản phẩm'}},
    {path: 'shopCard', component: ShoppingCartComponent, data: {breadcrumb: 'Giỏ hàng'}, canActivate: [authGuard]},
    {
      path: 'dashboard',
      component: DashboardLayoutComponent,
      data: {breadcrumb: 'Quản lý tài khoản'},
      // canActivate: [authGuard],
      children: [
        {path: '', component: DashboardComponent, data: {breadcrumb: 'Tổng quan'}},
        {path: 'orders', component: OrdersComponent, data: {breadcrumb: 'Lịch sử đơn hàng'}},
        {path: 'setting', component: SettingsComponent, data: {breadcrumb: 'Cài đặt'}},
        {path: 'shipping', component: ShippingComponent, data: {breadcrumb: 'Giao hàng'}},
      ]
    },
    {path: 'payment-success', component: PaymentSuccessComponent},
    {path: 'payment-cancel', component: PaymentCancelComponent},
    {path: '**', redirectTo: '', pathMatch: 'full'},
  ]
}];
