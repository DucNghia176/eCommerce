import {Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {ProductService} from "../../../../core/services/product.service";
import {ProductRequest} from "../../../../core/models/product.model";
import {FormsModule, NgForm} from "@angular/forms";
import {validateAndFocusFirstError} from "../../../../shared/utils/validation";
import {faArrowLeft, faCancel, faClose, faEdit, faSave} from "@fortawesome/free-solid-svg-icons";
import {CommonModule, Location} from "@angular/common";
import {ActivatedRoute, RouterModule} from "@angular/router";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {ToastComponent} from "../../../../shared/components/toast/toast.component";
import {ToastService} from "../../../../core/services/toast.service";
import {CategoryRequest, CategoryResponse} from "../../../../core/models/category.model";
import {CategoryService} from "../../../../core/services/category.service";
import {LoadingSpinnerComponent} from "../../../../shared/components/loading-spinner/loading-spinner.component";
import {finalize} from "rxjs";
import {TagBrandService} from "../../../../core/services/brand.service";
import {BrandResponse, TagResponse} from "../../../../core/models/TagBrand.model";
import {ImagePreview} from "../../../../core/models/image.model";
import {handleImagesSelected} from "../../../../shared/utils/file-upload.util";

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
  selectTag: number[] = [];
  brandId: number | null = null;
  images: ImagePreview[] = [];
  categoryName: string = '';
  isLoading = false;
  categories: CategoryResponse[] = [];
  brands: BrandResponse[] = [];
  tags: TagResponse[] = [];
  @ViewChild('formRef') formRef!: ElementRef;
  showMiniForm = false;
  protected readonly faArrowLeft = faArrowLeft;
  protected readonly faCancel = faCancel;
  protected readonly faSave = faSave;
  protected readonly faClose = faClose;
  protected readonly faEdit = faEdit;
  private productService = inject(ProductService);
  private toastService = inject(ToastService);
  private categoryService = inject(CategoryService);
  private location = inject(Location);
  private tagBrandService = inject(TagBrandService);
  private route = inject(ActivatedRoute);

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const categoryId = params['categoryId'];
      if (params['categoryId']) {
        this.categoryId = +categoryId;
      }
    });
    this.loadCategory();
    this.loadBrand();
    this.loadTag();
  }

  onFileSelected(event: Event): void {
    handleImagesSelected(event, (newImages) => {
      this.images = [...this.images, ...newImages];
    }, (msg) => {
      this.toastService.show(msg, "f");
    });
  }

  removeImage(index: number): void {
    this.images?.splice(index, 1);
  }

  addProduct(form: NgForm) {
    if (!validateAndFocusFirstError(form, this.formRef)) return;

    const request: ProductRequest = {
      name: this.name,
      description: this.description,
      price: this.price,
      discount: this.discount,
      categoryId: this.categoryId,
      tags: this.selectTag,
      unit: this.units,
      brandId: this.brandId
    }
    const files: File[] = this.images.map(image => image.file!);

    this.isLoading = true;

    this.productService.createProduct(request, files)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.toastService.show("Tạo sản phẩm thành công", "p")

          this.name = '';
          this.price = '';
          this.discount = '';
          this.units = '';
          this.description = '';
          this.images = [];
          this.tags = [];
          this.brandId = null;

          // Nếu muốn form vẫn valid và error cleared
          form.control.markAsPristine();
          form.control.markAsUntouched();
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

  loadBrand() {
    this.tagBrandService.getAllBrand().subscribe({
      next: (res) => {
        this.brands = res;
      },
      error: (err) => {
        this.toastService.show("Lấy dữ liệu thương hiệu thất bại " + err.message, "f")
      }
    })
  }

  loadTag() {
    this.tagBrandService.getAllTag().subscribe({
      next: (res) => {
        this.tags = res;
      },
      error: (err) => {
        this.toastService.show("Lấy dữ liệu nhãn thất bại " + err.message, "f")
      }
    })
  }

  addCategory() {
    const request: CategoryRequest = {
      name: this.categoryName,
      parentId: undefined,
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

  onTagChange(event: any, id: number) {
    if (event.target.checked) {
      this.selectTag.push(id);
    } else {
      this.selectTag = this.selectTag.filter(x => x !== id);
    }
  }
}
