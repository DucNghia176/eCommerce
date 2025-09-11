import {Component} from '@angular/core';
import {CommonModule} from "@angular/common";
import {RevenueCardComponent} from "../../components/dashboard/revenue-card/revenue-card.component";
import {OrderOverTimeComponent} from "../../components/dashboard/order-over-time/order-over-time.component";
import {
  RecentTransactionsComponent
} from "../../components/dashboard/recent-transactions/recent-transactions.component";
import {TopProductComponent} from "../../components/dashboard/top-product/top-product.component";
import {PaymentStatus} from "../../../../shared/status/payment-status";
import {NgApexchartsModule} from "ng-apexcharts";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RevenueCardComponent, OrderOverTimeComponent, RecentTransactionsComponent, TopProductComponent, NgApexchartsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  stats = [
    {title: 'Total Revenue', value: '$10.54', growth: '22.45%'},
    {title: 'Orders', value: '1,056', growth: '15.34%'},
    {title: 'New Users', value: '1,650', growth: '13.45%'},
    {title: 'Existing Users', value: '9,653', growth: '22.45%'}
  ];

  data21 = [10, 20, 30, 40, 20, 50];
  data22 = [5, 15, 35, 25, 45, 30];
  labels = ['4am', '6am', '8am', '10am', '12pm', '2pm'];

  transactions = [
    {name: 'Jaganrath S.', date: '24.05.2023', amount: '$124.97', status: PaymentStatus.Paid},
    {name: 'Anand G.', date: '23.05.2023', amount: '$55.42', status: PaymentStatus.Pending},
    {name: 'Kartik S.', date: '23.05.2023', amount: '$89.90', status: PaymentStatus.Paid},
  ];

  topProducts = [
    {name: 'Men Grey Hoodie', price: '49.90', units: 204, image: 'assets/products/grey-hoodie.png'},
    {name: 'Women Striped T-Shirt', price: '34.90', units: 155, image: 'assets/products/striped-tshirt.png'},
    {name: 'Women White T-Shirt', price: '40.90', units: 120, image: 'assets/products/white-tshirt.png'}
  ];
}
