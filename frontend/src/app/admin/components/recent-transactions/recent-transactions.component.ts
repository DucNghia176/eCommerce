import {Component, Input} from '@angular/core';
import {CommonModule} from "@angular/common";
import {PaymentStatus} from "../../../shared/status/payment-status";

@Component({
  selector: 'app-recent-transactions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './recent-transactions.component.html',
  styleUrl: './recent-transactions.component.scss'
})
export class RecentTransactionsComponent {
  @Input() transactions: {
    name: string;
    date: string;
    amount: string;
    status: PaymentStatus;
  }[] = [];
  protected readonly PaymentStatus = PaymentStatus;
}
