import {Component} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {NgForOf, NgIf} from "@angular/common";
import {PageComponent} from "../../../shared/components/page/page.component";
import {faAdd, faFileExport, faSearch, faTrash} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [
    FaIconComponent,
    NgForOf,
    NgIf,
    PageComponent
  ],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent {

  protected readonly faFileExport = faFileExport;
  protected readonly faAdd = faAdd;
  protected readonly faSearch = faSearch;
  protected readonly faTrash = faTrash;
}
