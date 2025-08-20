import {Component, ElementRef, inject, OnInit, ViewChild} from '@angular/core';
import {CategoryRequest, CategoryResponse} from "../../../../core/models/category.model";
import {BrandResponse, TagResponse} from "../../../../core/models/TagBrand.model";
import {ProductService} from "../../../../core/services/product.service";
import {ToastService} from "../../../../core/services/toast.service";
import {CategoryService} from "../../../../core/services/category.service";
import {Location, NgForOf, NgIf} from "@angular/common";
import {TagBrandService} from "../../../../core/services/brand.service";
import {FormsModule, NgForm, ReactiveFormsModule} from "@angular/forms";
import {validateAndFocusFirstError} from "../../../../shared/utils/validation";
import {ProductRequest} from "../../../../core/models/product.model";
import {finalize} from "rxjs";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {LoadingSpinnerComponent} from "../../../../shared/components/loading-spinner/loading-spinner.component";
import {ActivatedRoute} from "@angular/router";
import {ToastComponent} from "../../../../shared/components/toast/toast.component";
import {faArrowLeft, faClose, faSave} from "@fortawesome/free-solid-svg-icons";
import {ImagePreview} from "../../../../core/models/image.model";

@Component({
  selector: 'app-update-product',
  standalone: true,
  imports: [
    FaIconComponent,
    FormsModule,
    LoadingSpinnerComponent,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    ToastComponent
  ],
  templateUrl: './update-product.component.html',
  styleUrl: './update-product.component.scss'
})
export class UpdateProductComponent implements OnInit {
  productId!: number;
  name: string = '';
  price: string = '';
  discount: string = '';
  units: string = '';
  categoryId: number | null = null;
  description: string = '';
  selectTag: number[] = [];
  selectBrand: number | null = null;
  brandId: number | null = null;
  images: ImagePreview[] = [];
  categoryName: string = '';
  isLoading = false;
  categories: CategoryResponse[] = [];
  brands: BrandResponse[] = [];
  tags: TagResponse[] = [];
  @ViewChild('formRef') formRef!: ElementRef;
  showMiniForm = false;
  protected readonly faClose = faClose;
  protected readonly faSave = faSave;
  protected readonly faArrowLeft = faArrowLeft;
  private route = inject(ActivatedRoute);
  private productService = inject(ProductService);
  private toastService = inject(ToastService);
  private categoryService = inject(CategoryService);
  private location = inject(Location);
  private tagBrandService = inject(TagBrandService);

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.productId = +id;
        this.loadProduct();
      }
    });
    this.loadCategory();
    this.loadBrand();
    this.loadTag();
  }

  async loadProduct() {
    this.productService.getProductById(this.productId).subscribe({
      next: (product) => {
        console.log('Sản phẩm lấy được:', product);
        this.name = product.name;
        this.price = product.price;
        this.discount = product.discountPrice || '';
        this.units = product.unit || '';
        this.description = product.description || '';
        this.categoryId = product.categoryId ? +product.categoryId : null;
        this.brandId = product.brand ? product.brand.id : null;
        this.selectTag = product.tags?.map(tag => +tag.id) || [];
        this.images = (product.imageUrls || []).map(url => ({src: url}));
      },
      error: (err) => console.error(err)
    });
  }


  async urlToFile(url: string, filename: string): Promise<File> {
    const response = await fetch(url);
    const blob = await response.blob();
    return new File([blob], filename, {type: blob.type});
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;
    else {
      const files = Array.from(input.files)
      for (const file of files) {
        if (!file.type.startsWith('image/')) {
          this.toastService.show("Chỉ được phép chọn tệp ảnh (jpg, png, gif...)", "f");
          continue;
        }

        const reader = new FileReader();
        reader.onload = () => {
          this.images.push({src: reader.result as string, file: file});
        };
        reader.readAsDataURL(file);
      }

      input.value = '';
    }
  }

  removeImage(index: number): void {
    this.images.splice(index, 1);
  }

  async updateProduct(form: NgForm) {
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
    const files: File[] = [];
    for (const image of this.images) {
      if (image.file) {
        files.push(image.file);
      } else {
        const file = await this.urlToFile(image.src, `image_${this.images.indexOf(image) + 1}.jpg`);
        files.push(file);
      }
    }
    this.isLoading = true;

    this.productService.updateProduct(this.productId, request, files)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: () => {
          this.toastService.show("Cập nhật sản phẩm thành công", "p")
          this.loadProduct()
          this.loadCategory();
          this.loadBrand();
          this.loadTag();
        },
        error: (err) => {
          this.toastService.show(`Cập nhật sản phẩm thất bại: ${err.message}`, "f");
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
