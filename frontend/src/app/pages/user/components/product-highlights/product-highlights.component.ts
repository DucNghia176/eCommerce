import {Component} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faBox, faCreditCard, faHeadphones, faTrophy} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-product-highlights',
  standalone: true,
  imports: [
    FaIconComponent
  ],
  templateUrl: './product-highlights.component.html',
  styleUrl: './product-highlights.component.scss'
})
export class ProductHighlightsComponent {

  protected readonly faCreditCard = faCreditCard;
  protected readonly faBox = faBox;
  protected readonly faHeadphones = faHeadphones;
  protected readonly faTrophy = faTrophy;
}
