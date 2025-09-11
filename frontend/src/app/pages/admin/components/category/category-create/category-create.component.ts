import {Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {CategoryService} from "../../../../../core/services/category.service";
import {ToastService} from "../../../../../core/services/toast.service";
import {ImagePreview} from "../../../../../core/models/image.model";
import {CommonModule, Location} from "@angular/common";
import {FormsModule, NgForm} from "@angular/forms";
import {finalize} from "rxjs";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faArrowLeft, faClose, faEdit, faSave} from "@fortawesome/free-solid-svg-icons";
import {LoadingSpinnerComponent} from "../../../../../shared/components/loading-spinner/loading-spinner.component";
import {ToastComponent} from "../../../../../shared/components/toast/toast.component";
import {CategoryRequest, CategoryResponse} from "../../../../../core/models/category.model";
import {validateAndFocusFirstError} from "../../../../../shared/utils/validation";
import {handleImagesSelected} from "../../../../../shared/utils/file-upload.util";

@Component({
  selector: 'app-category-create',
  standalone: true,
  imports: [CommonModule, FormsModule, FaIconComponent, LoadingSpinnerComponent, ToastComponent],

  templateUrl: './category-create.component.html',
  styleUrl: './category-create.component.scss'
})
export class CategoryCreateComponent implements OnInit {
  name: string = '';
  parentId?: number;
  isVisible: number = 1;
  isLoading = false;
  categories: CategoryResponse[] = [];
  image: ImagePreview | null = null;
  @ViewChild('formRef') formRef!: ElementRef;
  isOpen = false;
  selectedCategory: any = null;
  submitted = false;
  protected readonly faArrowLeft = faArrowLeft;
  protected readonly faClose = faClose;
  protected readonly faSave = faSave;
  protected readonly faEdit = faEdit;
  private categoryService = inject(CategoryService)
  private location = inject(Location);
  private toastService = inject(ToastService);

  ngOnInit() {
    this.loadCategory();
  }

  createCategory(form: NgForm) {
    if (!validateAndFocusFirstError(form, this.formRef)) return;

    this.submitted = true;

    const request: CategoryRequest = {
      name: this.name,
      parentId: this.parentId,
      isVisible: this.isVisible
    }

    const file: File | undefined = this.image?.file!;
    if (form.invalid) return;
    this.isLoading = true;

    this.categoryService.createCategory(request, file)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.toastService.show("Tạo danh mục thành công", "p")

          form.resetForm();
        }, error: (err) => {
          this.toastService.show("Lỗi tạo danh mục " + err.message, "f")

        }
      })
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

  onFileSelected(event: Event): void {
    handleImagesSelected(event, (images) => {
      this.image = images[0];
    }, (msg) => {
      alert(msg);
    });
  }

  goBack() {
    this.location.back();
  }

  removeImage(): void {
    this.image = null as any;
  }

  selectCategory(category: any) {
    this.selectedCategory = category;
    this.parentId = category ? category.id : null;
    this.isOpen = false;
  }
}
