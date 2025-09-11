import {Component, inject, OnInit} from '@angular/core';
import {CommonModule} from "@angular/common";
import {CategoryResponse} from "../../../../core/models/category.model";
import {CategoryService} from "../../../../core/services/category.service";
import {finalize} from "rxjs";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {RouterLink} from "@angular/router";
import {faAdd, faFileExport} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-category',
  standalone: true,
  imports: [CommonModule, FaIconComponent, RouterLink],
  templateUrl: './category.component.html',
  styleUrl: './category.component.scss'
})
export class CategoryComponent implements OnInit {
  categories: CategoryResponse[] = [];
  errorMessages: string | null = null;
  isLoading = false;
  protected readonly faFileExport = faFileExport;
  protected readonly faAdd = faAdd;
  private categoryService = inject(CategoryService);

  ngOnInit() {
    this.loadCategories();
  }

  private loadCategories() {
    this.isLoading = true;
    this.categoryService.getAllCategory()
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (data) => {
          this.categories = data;
          this.errorMessages = null;
        },
        error: (err) => {
          this.categories = [];
          this.errorMessages = err.message;
        }
      });
  }
}
