import {Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {CommonModule, Location, NgOptimizedImage} from "@angular/common";
import {CategoryService} from "../../../../../core/services/category.service";
import {PageSize} from "../../../../../shared/status/page-size";
import {finalize} from "rxjs";
import {ProductSummary} from "../../../../../core/models/product-summary.model";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {CategoryRequest, CategoryResponse} from "../../../../../core/models/category.model";
import {ImagePreview} from "../../../../../core/models/image.model";
import {ToastService} from "../../../../../core/services/toast.service";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faAdd, faArrowLeft, faClose, faEdit, faSave} from "@fortawesome/free-solid-svg-icons";
import {ToastComponent} from "../../../../../shared/components/toast/toast.component";
import {PageComponent} from "../../../../../shared/components/page/page.component";
import {FormsModule, NgForm, ReactiveFormsModule} from "@angular/forms";
import {validateAndFocusFirstError} from "../../../../../shared/utils/validation";

@Component({
  selector: 'app-category-detail',
  standalone: true,
  imports: [CommonModule, FaIconComponent, ToastComponent, PageComponent, ReactiveFormsModule, FormsModule, RouterLink, NgOptimizedImage],
  templateUrl: './category-detail.component.html',
  styleUrl: './category-detail.component.scss'
})
export class CategoryDetailComponent implements OnInit {
  products: ProductSummary[] = [];
  categories: CategoryResponse[] = [];
  categoryId!: number;
  category!: CategoryResponse;
  categoryName: string = '';
  parentId!: number;
  isVisible!: number;
  image: ImagePreview | null = null;
  isLoading = false;
  @ViewChild('formRef') formRef!: ElementRef;
  totalPages = 0;
  currentPage = 0;
  totalItems: number = 0;
  pageSize: number = 10;
  pageSizes: number[] = PageSize
  errorMessages: string | null = null;
  isOpen = false;
  selectedCategory: any = null;
  protected readonly faArrowLeft = faArrowLeft;
  protected readonly faClose = faClose;
  protected readonly faSave = faSave;
  protected readonly faAdd = faAdd;
  protected readonly faEdit = faEdit;
  private location = inject(Location);
  private categoryService = inject(CategoryService);
  private route = inject(ActivatedRoute);
  private toastService = inject(ToastService);

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.categoryId = +id;
        this.loadProductsByCategory();
        void this.loadCategory();
        this.loadCategories();
      }
    })
  }

  loadProductsByCategory(page: number = 0): void {
    this.isLoading = true;
    this.categoryService.getProductsByCategory(this.categoryId, page, this.pageSize)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (data) => {
          const products = data.content;
          this.currentPage = data.number;
          this.totalItems = data.totalElements;
          this.totalPages = data.totalPages;
          this.products = products;
          this.errorMessages = null;
        },
        error: (err) => {
          this.products = [];
          this.errorMessages = err.message;
        }
      });
  }

  async loadCategory() {
    this.isLoading = true;
    this.categoryService.getCategoryById(this.categoryId)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (data) => {
          this.category = data;
          this.categoryName = data.name;
          this.parentId = data.parentId;
          this.image = data.image ? {src: data.image} : null;
          this.isVisible = data.isVisible;
          this.errorMessages = null;
        }
      })
  }

  async updateProduct(form: NgForm) {
    if (!validateAndFocusFirstError(form, this.formRef)) return;

    const request: CategoryRequest = {
      name: this.categoryName,
      parentId: this.parentId,
      isVisible: this.isVisible ? 1 : 0
    }
    let file: File | undefined = undefined;
    if (this.image?.file) {
      file = this.image.file;
    } else if (this.image?.src) {
      file = await this.urlToFile(this.image.src, "image.jpg");
    }
    this.isLoading = true;

    this.categoryService.updateCategory(this.categoryId, request, file)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.toastService.show("Cập nhật danh mục thành công", "p")
          this.loadProductsByCategory()
          this.loadCategory();
        },
        error: (err) => {
          this.toastService.show(`Cập nhật danh mục thất bại: ${err.message}`, "f");
        }
      });
  }

  async urlToFile(url: string, filename: string): Promise<File> {
    const response = await fetch(url);
    const blob = await response.blob();
    return new File([blob], filename, {type: blob.type});
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0]; // chỉ lấy 1 ảnh đầu tiên

    if (!file.type.startsWith('image/')) {
      this.toastService.show("Chỉ được phép chọn tệp ảnh (jpg, png, gif...)", "f");
      input.value = '';
      return;
    }


    const reader = new FileReader();
    reader.onload = () => {
      // chỉ giữ 1 ảnh duy nhất
      this.image = {src: reader.result as string, file: file};
    };
    reader.readAsDataURL(file);
    input.value = '';
  }

  goBack() {
    this.location.back();
  }

  removeImage(): void {
    this.image = null as any;
  }

  onPageSizeChange(newSize: number) {
    this.pageSize = newSize;
    this.loadProductsByCategory(0);
  }

  selectCategory(category: any) {
    this.selectedCategory = category;
    this.parentId = category ? category.id : null;
    this.isOpen = false;
  }

  private loadCategories() {
    this.isLoading = true;
    this.categoryService.getAllCategory()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (data) => {
          const excludedIds = this.getAllRelatedCategoryIds(this.categoryId, data);
          this.categories = data.filter(c => !excludedIds.has(c.id));
          this.errorMessages = null;
        },
        error: (err) => {
          this.categories = [];
          this.errorMessages = err.message;
        }
      });
  }

  private getAllRelatedCategoryIds(categoryId: number, categories: CategoryResponse[]): Set<number> {
    const ids = new Set<number>();
    const map = new Map<number, CategoryResponse>();

    // Tạo map để tra cứu nhanh
    categories.forEach(c => map.set(c.id, c));

    const queue: number[] = [categoryId];

    while (queue.length > 0) {
      const currentId = queue.shift()!;
      if (!ids.has(currentId)) {
        ids.add(currentId);
        // Tìm các category có parent là currentId
        categories.forEach(c => {
          if (c.parentId === currentId && !ids.has(c.id)) {
            queue.push(c.id);
          }
        });
      }
    }

    return ids;
  }
}
