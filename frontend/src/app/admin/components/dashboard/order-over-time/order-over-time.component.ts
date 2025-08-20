import {Component, Input} from '@angular/core';
import {
  ApexAxisChartSeries,
  ApexChart,
  ApexDataLabels,
  ApexGrid,
  ApexStroke,
  ApexTitleSubtitle,
  ApexXAxis,
  NgApexchartsModule
} from "ng-apexcharts";

export type ChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  title: ApexTitleSubtitle;
  stroke: ApexStroke;
  dataLabels: ApexDataLabels;
  grid: ApexGrid;
};

@Component({
  selector: 'app-order-over-time',
  standalone: true,
  imports: [NgApexchartsModule],
  templateUrl: './order-over-time.component.html',
  styleUrl: './order-over-time.component.scss'
})


export class OrderOverTimeComponent {
  @Input() data21: number[] = [];
  @Input() data22: number[] = [];
  @Input() labels: string[] = [];
  
  chartOptions = {
    series: [
      {
        name: 'Orders',
        data: [10, 41, 35, 51, 49, 62, 69]
      }
    ],
    chart: {
      height: 350,
      type: 'line'
    } as ApexChart,
    dataLabels: {
      enabled: false
    } as ApexDataLabels,
    stroke: {
      curve: 'smooth',
      width: 2
    } as ApexStroke,
    title: {
      text: 'Orders Over Time',
      align: 'left'
    } as ApexTitleSubtitle,
    xaxis: {
      categories: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    } as ApexXAxis,
    grid: {
      row: {
        colors: ['#f3f3f3', 'transparent'],
        opacity: 0.5
      }
    } as ApexGrid
  };

  ngOnInit(): void {
    this.chartOptions = {
      series: [
        {
          name: '2021',
          data: this.data21
        },
        {
          name: '2022',
          data: this.data22
        }
      ],
      chart: {
        type: 'line',
        height: 350
      },
      stroke: {
        width: 3,
        curve: 'smooth'
      },
      dataLabels: {
        enabled: false
      },
      title: {
        text: 'Orders Over Time',
        align: 'left'
      },
      xaxis: {
        categories: this.labels
      },
      grid: {
        row: {
          colors: ['#f3f3f3', 'transparent'],
          opacity: 0.5
        }
      }
    };
  }
}
