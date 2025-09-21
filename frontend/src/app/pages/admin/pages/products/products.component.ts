import {Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {CommonModule, NgForOf, NgIf, NgOptimizedImage} from "@angular/common";
import {PageComponent} from "../../../../shared/components/page/page.component";
import {faAdd, faEdit, faFileExport, faSearch, faTrash, faWarehouse} from "@fortawesome/free-solid-svg-icons";
import {PageSize} from "../../../../shared/status/page-size";
import {ProductService} from "../../../../core/services/product.service";
import {RouterLink} from "@angular/router";
import {SelectionService} from "../../../../core/services/selection.service";
import {debounceTime, finalize, forkJoin, Subject, switchMap, takeUntil} from "rxjs";
import {DialogService} from "../../../../core/services/dialog.service";
import {FormsModule} from "@angular/forms";
import {ProductResponse, ProductSearchRequest} from "../../../../core/models/product.model";
import {LoadingSpinnerComponent} from "../../../../shared/components/loading-spinner/loading-spinner.component";
import {ToastComponent} from "../../../../shared/components/toast/toast.component";
import {InventoryService} from "../../../../core/services/inventory.service";
import {ToastService} from "../../../../core/services/toast.service";
import {InventoryRequest} from "../../../../core/models/inventory.model";

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [
    FaIconComponent,
    NgForOf,
    NgIf,
    PageComponent,
    RouterLink,
    LoadingSpinnerComponent,
    ToastComponent,
    FormsModule,
    CommonModule,
    NgOptimizedImage
  ],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent implements OnInit {

  @ViewChild('quantityInput') quantityInput!: ElementRef;
  products: ProductResponse[] = [];
  isModalOpen = false;
  errorMessage: string | null = null;
  images?: File[];
  totalPages = 0;
  currentPage = 0;
  totalItems: number = 0;
  pageSize: number = 10;
  addQuantity?: number;
  skuCode: string = '';
  selectedProduct: ProductResponse | null = null;
  isQuantityModalOpen = false;
  isConfirmOpen = false;
  name: string = '';
  searchTerm: string = '';
  searchTerm$ = new Subject<string>();
  category: string = '';
  status: string = '';
  pageSizes: number[] = PageSize
  isLoading = false
  public selectionService = inject(SelectionService<number>);
  protected readonly faFileExport = faFileExport;
  protected readonly faAdd = faAdd;
  protected readonly faTrash = faTrash;
  protected readonly faEdit = faEdit;
  protected readonly faWarehouse = faWarehouse;
  protected readonly search = faSearch;
  private productService = inject(ProductService);
  private dialogService = inject(DialogService);
  private inventoryService = inject(InventoryService);
  private toastService = inject(ToastService);
  private destroy$ = new Subject<void>();

  ngOnInit() {
    this.searchTerm$
      .pipe(
        debounceTime(300),
        switchMap(term => {
          const request: ProductSearchRequest = {name: term};
          return this.productService.searchProduct(request, 0, 10);
        }),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (data) => {
          const allProduct = data.content;
          this.currentPage = data.number;
          this.totalItems = data.totalElements;
          this.totalPages = data.totalPages;
          this.products = allProduct;
          this.errorMessage = null;
        },
        error: (err) => {
          this.toastService.show("Lỗi hệ thống: " + err, "f");
        }
      });
    this.loadProduct();
  }

  public loadProduct(page: number = 0): void {
    this.productService.getAllProduct(page, this.pageSize)
      .subscribe({
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

  openProductDetail(product: ProductResponse) {
    this.selectedProduct = product;
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  // Khi nhấn icon
  openQuantityModal(product: any) {
    this.selectedProduct = product;
    this.skuCode = product.skuCode;
    this.isQuantityModalOpen = true;
    setTimeout(() => {
      if (this.quantityInput) {
        this.quantityInput.nativeElement.focus();
      }
    }, 100);
  }

// Đóng modal
  closeQuantityModal() {
    this.isQuantityModalOpen = false;
    this.selectedProduct = null;
    this.addQuantity = undefined;
  }

  updateQuantity() {
    if (!this.addQuantity || this.addQuantity <= 0) {
      this.toastService.show("Vui lòng nhập số lượng hợp lệ", "f");
      return;
    }

    const request: InventoryRequest = {
      skuCode: this.skuCode,
      quantity: this.addQuantity
    }
    this.inventoryService.updateInventory(request)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.toastService.show("Cập nhật số lượng thàng công", "p")
          this.closeQuantityModal();
          this.loadProduct();

        }, error: (err) => {
          this.toastService.show("Cập nhật số lượng thất bại" + err, "f")
        }
      })
  }

  searchProduct(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.searchTerm$.next(value);
  }
}
