import {Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {ProductService} from "../../../core/services/product.service";
import {ProductRequest} from "../../../core/models/product.model";
import {FormsModule, NgForm} from "@angular/forms";
import {validateAndFocusFirstError} from "../../../shared/utils/validation";
import {faArrowLeft, faCancel, faClose, faSave} from "@fortawesome/free-solid-svg-icons";
import {CommonModule, Location} from "@angular/common";
import {RouterModule} from "@angular/router";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {ToastComponent} from "../../../shared/components/toast/toast.component";
import {ToastService} from "../../../core/services/toast.service";
import {CategoryRequest, CategoryResponse} from "../../../core/models/category.model";
import {CategoryService} from "../../../core/services/category.service";
import {LoadingSpinnerComponent} from "../../../shared/components/loading-spinner/loading-spinner.component";
import {finalize} from "rxjs";

@Component({
  selector: 'app-create-product',
  standalone: true,
  imports: [CommonModule, RouterModule, FaIconComponent, FormsModule, ToastComponent, LoadingSpinnerComponent],
  templateUrl: './create-product.component.html',
  styleUrl: './create-product.component.scss'
})
export class CreateProductComponent implements OnInit {
  name: string = '';
  price: string = '';
  discount: string = '';
  units: string = '';
  categoryId: number | null = null;
  description: string = '';
  tags: string[] = [];
  brandId: number | null = null;
  images: File[] = [];
  imagesPreview: File[] = [];
  categoryName: string = '';
  brandName: string = '';
  isLoading = false;
  categories: CategoryResponse[] = [];
  @ViewChild('formRef') formRef!: ElementRef;
  showMiniForm = false;
  protected readonly faArrowLeft = faArrowLeft;
  protected readonly faCancel = faCancel;
  protected readonly faSave = faSave;
  protected readonly faClose = faClose;
  private productService = inject(ProductService);
  private toastService = inject(ToastService);
  private categoryService = inject(CategoryService);
  private location = inject(Location);

  ngOnInit() {
    this.loadCategory();
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    const files = Array.from(input.files);
    for (const file of files) {
      if (!file.type.startsWith('image/')) {
        this.toastService.show("Chỉ được phép chọn tệp ảnh (jpg, png, gif...)", "f");
        this.images.push(
          file
        );
        continue;
      }

      this.images.push(file);

      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagesPreview.push(e.target.result);
      };
      reader.readAsDataURL(file);
    }

    input.value = '';
  }

  removeImage(index: number): void {
    this.images?.splice(index, 1);
    this.imagesPreview?.splice(index, 1);
  }

  addProduct(form: NgForm) {
    if (!validateAndFocusFirstError(form, this.formRef)) return;

    const request: ProductRequest = {
      name: this.name,
      description: this.description,
      price: this.price,
      discount: this.discount,
      categoryId: this.categoryId,
      tags: this.tags,
      unit: this.units,
      brandId: this.brandId
    }
    const images = this.images;

    this.isLoading = true;

    this.productService.createProduct(request, images)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.toastService.show("Tạo sản phẩm thành công", "p")

          form.resetForm(); // reset toàn bộ form
          this.images = [];
          this.imagesPreview = [];
          this.categoryId = null;
          this.tags = [];
          this.units = '';
          this.brandId = null;
        },
        error: (err) => {
          this.toastService.show(`Tạo sản phẩm thất bại: ${err.message}`, "f");
        }
      });
  }

  loadCategory() {
    this.categoryService.getAllCategory().subscribe({
      next: (res) => {
        this.categories = res;
      },
      error: (err) => {
        this.toastService.show("Lấy dữ liệu danh mục thất bại " + err.message, "f")
      }
    });
  }

  addCategory() {
    const request: CategoryRequest = {
      name: this.categoryName,
      parentId: undefined,
      image: undefined,
    }
    this.categoryService.createCategory(request).subscribe({
      next: () => {
        this.toastService.show("Thêm danh mục thành công", "p")
        this.loadCategory();
      },
      error: (err) => {
        this.toastService.show(`Thêm danh mục thất bại: ${err.message}`, "f");
      }
    });
  }

  goBack() {
    this.location.back();
  }

  toggleMiniForm() {
    this.showMiniForm = !this.showMiniForm;
  }

  saveMiniProduct() {
    if (!this.categoryName.trim()) return;
    this.addCategory();
    // Reset form
    this.categoryName = '';
    this.showMiniForm = false;
  }
}
