import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-revenue-card',
  standalone: true,
  imports: [],
  templateUrl: './revenue-card.component.html',
  styleUrl: './revenue-card.component.scss'
})
export class RevenueCardComponent {
  @Input() title = '';
  @Input() value = '';
  @Input() growth = '';

}
