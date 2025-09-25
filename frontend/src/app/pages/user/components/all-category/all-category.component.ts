import {Component, ElementRef, HostListener, Input} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {CurrencyPipe, NgClass, NgForOf, NgIf, NgStyle, SlicePipe} from "@angular/common";
import {faChevronDown, faChevronRight} from "@fortawesome/free-solid-svg-icons";
import {ChildCategoryResponse} from "../../../../core/models/category.model";

@Component({
  selector: 'app-all-category',
  standalone: true,
  imports: [
    FaIconComponent,
    NgForOf,
    NgIf,
    NgClass,
    NgStyle,
    SlicePipe,
    CurrencyPipe,
  ],
  templateUrl: './all-category.component.html',
  styleUrl: './all-category.component.scss'
})
export class AllCategoryComponent {

  @Input() categories?: ChildCategoryResponse[];
  showCategory = false;
  allCategoryOpen = false;
  allCategorySelected = false;
  activeCategoryId: number | null = null;
  hoveredCategoryId: number | null = null;
  selectedSubCategoryId: number | null = null;
  protected readonly faChevronRight = faChevronRight;
  protected readonly faChevronDown = faChevronDown;

  constructor(private elRef: ElementRef) {
  }

  toggleCategory() {
    this.showCategory = !this.showCategory;
    this.allCategoryOpen = this.showCategory;
    if (!this.showCategory) {
      this.activeCategoryId = null;
      this.selectedSubCategoryId = null;
      this.allCategorySelected = false;
    } else if (!this.activeCategoryId) {
      this.allCategorySelected = true;
    }
  }

  selectSubCategory(catId: number, subId: number) {
    this.activeCategoryId = catId;
    this.selectedSubCategoryId = subId;
    this.allCategoryOpen = true;
    this.allCategorySelected = false;
  }

  isCategoryActive(): boolean {
    return this.allCategoryOpen;
  }

  onMouseEnterCategory(catId: number) {
    this.activeCategoryId = catId;
    this.hoveredCategoryId = catId;
  }

  onMouseLeaveCategory(catId: number) {
    if (this.hoveredCategoryId === catId) {
      this.hoveredCategoryId = null;
    }
    this.activeCategoryId = null;
  }

  showChevronRight(catId: number): boolean {
    return this.hoveredCategoryId === catId;
  }

  isCategoryHover(catId: number): boolean {
    return this.hoveredCategoryId === catId;
  }

  @HostListener('document:click', ['$event'])
  clickOutside(event: Event) {
    if (!this.elRef.nativeElement.contains(event.target)) {
      this.showCategory = false;
      this.allCategoryOpen = false;
    }
  }
}
