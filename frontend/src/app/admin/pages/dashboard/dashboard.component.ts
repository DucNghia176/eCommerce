import {Component} from '@angular/core';
import {RevenueCardComponent} from "../../components/revenue-card/revenue-card.component";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RevenueCardComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {

}
