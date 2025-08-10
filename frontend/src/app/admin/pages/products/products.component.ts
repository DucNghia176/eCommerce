import {Component, inject, OnInit} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {NgForOf, NgIf} from "@angular/common";
import {PageComponent} from "../../../shared/components/page/page.component";
import {faAdd, faFileExport, faSearch, faTrash} from "@fortawesome/free-solid-svg-icons";
import {ProductResponse} from "../../../core/models/product.model";
import {PageSize} from "../../../shared/status/page-size";
import {ProductService} from "../../../core/services/product.service";
import {RouterLink} from "@angular/router";
import {SelectionService} from "../../../core/services/selection.service";
import {forkJoin} from "rxjs";
import {DialogService} from "../../../core/services/dialog.service";

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [
    FaIconComponent,
    NgForOf,
    NgIf,
    PageComponent,
    RouterLink
  ],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent implements OnInit {

  products: ProductResponse[] = [];
  errorMessage: string | null = null;
  images?: File[];
  totalPages = 0;
  currentPage = 0;
  totalItems: number = 0;
  pageSize: number = 10;
  inventory: number = 0;
  isConfirmOpen = false;
  price: number = 0;
  name: string = '';
  category: string = '';
  status: string = '';
  pageSizes: number[] = PageSize
  public selectionService = inject(SelectionService<number>);
  protected readonly faFileExport = faFileExport;
  protected readonly faAdd = faAdd;
  protected readonly faSearch = faSearch;
  protected readonly faTrash = faTrash;
  private productService = inject(ProductService)
  private dialogService = inject(DialogService);

  ngOnInit() {
    this.loadProduct();
  }

  public loadProduct(page: number = 0): void {
    this.productService.getAllProduct(page, this.pageSize).subscribe({
      next: (data) => {
        const allProduct = data.content;
        this.currentPage = data.number;
        this.totalItems = data.totalElements;
        this.totalPages = data.totalPages;

        this.products = allProduct;
        this.errorMessage = null;
      },
      error: (err) => {
        this.products = [];
        this.errorMessage = err.message;
      }
    });
  }

  onPageSizeChange(newSize: number) {
    this.pageSize = newSize;
    this.loadProduct(0);
  }

  onCheckbox(id: number, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    const ids = this.products.map(product => product.id);
    this.selectionService.toggleSelection(id, checked, ids);
  }

  onCheckboxAll(event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    const ids = this.products.map(product => product.id);
    this.selectionService.toggleAll(checked, ids);
  }

  deleteProduct() {
    const ids = this.selectionService.getSelectedItems();
    console.log('Selected IDs to delete:', ids);

    const deleteRequest =
      ids.map(id => this.productService.deleteProduct(id));
    this.dialogService.confirmDelete(`Bạn có chắc chắn muốn xóa ${ids.length} sản phẩm không?`)
      .subscribe((confirmed) => {
        if (confirmed) {
          forkJoin(deleteRequest).subscribe({
            next: () => {
              this.loadProduct();
              this.selectionService.clearSelection();
              this.errorMessage = null;
            },
            error: (err) => {
              this.errorMessage = err.message;
            }
          });
        }
      });
  }
}
