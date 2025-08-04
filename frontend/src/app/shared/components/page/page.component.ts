import {Component, EventEmitter, Input, Output} from '@angular/core';
import {PageSize} from "../../status/page-size";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './page.component.html',
  styleUrl: './page.component.scss'
})
export class PageComponent {
  @Input() currentPage: number = 0;
  @Input() totalItems: number = 0;
  @Input() pageSize: number = PageSize[0];
  @Input() pageSizes: number[] = PageSize;

  @Output() pageChange = new EventEmitter<{ page: number }>();
  @Output() pageSizeChange = new EventEmitter<number>();
  protected readonly Math = Math;

  get totalPages(): number {
    return Math.ceil(this.totalItems / this.pageSize);
  }

  get pageNumbers(): number[] {
    const total = this.totalPages;
    const current = this.currentPage;
    const delta = 2;
    let start = Math.max(0, current - delta);
    let end = Math.min(total - 1, current + delta);

    // điều chỉnh nếu gần đầu hoặc gần cuối
    if (current <= delta) {
      end = Math.min(4, total - 1);
    }
    if (current >= total - 1 - delta) {
      start = Math.max(total - 5, 0);
    }

    const pages: number[] = [];
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.pageChange.emit({page});
    }
  }

  onPageSizeChange(event: Event): void {
    const value = +(event.target as HTMLSelectElement).value;
    this.pageSizeChange.emit(value);
    this.pageChange.emit({page: 0}); // reset về trang đầu
  }

}
