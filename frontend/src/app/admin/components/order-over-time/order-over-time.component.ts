import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-order-over-time',
  standalone: true,
  imports: [],
  templateUrl: './order-over-time.component.html',
  styleUrl: './order-over-time.component.scss'
})
export class OrderOverTimeComponent {
  @Input() labels: string[] = [];
  @Input() data21: number[] = [];
  @Input() data22: number[] = [];
}
