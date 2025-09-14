import {Component} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faFacebook, faInstagram, faPinterest, faReddit, faTwitter, faYoutube} from "@fortawesome/free-brands-svg-icons";
import {
  faCartShopping,
  faCircleInfo,
  faHeadset,
  faHeart,
  faLocationDot,
  faPhoneVolume,
  faRotate,
  faSearch,
  faUser
} from "@fortawesome/free-solid-svg-icons";
import {RouterLink} from "@angular/router";
import {AllCategoryComponent} from "../all-category/all-category.component";

interface Category {
  id: number;
  name: string;
  children?: Category[];
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    FaIconComponent,
    RouterLink,
    AllCategoryComponent
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  categories: Category[] = [
    {
      id: 1,
      name: 'Electronics',
      children: [
        {id: 11, name: 'Phones'},
        {id: 12, name: 'Laptops'},
        {id: 13, name: 'Cameras'},
      ],
    },
    {
      id: 2,
      name: 'Fashion',
      children: [
        {id: 21, name: 'Men'},
        {id: 22, name: 'Women'},
        {id: 23, name: 'Kids'},
      ],
    },
    {
      id: 3,
      name: 'Home & Garden',
      children: [
        {id: 31, name: 'Furniture'},
        {id: 32, name: 'Kitchen'},
        {id: 33, name: 'Decor'},
      ],
    },
  ];
  protected readonly faTwitter = faTwitter;
  protected readonly faFacebook = faFacebook;
  protected readonly faPinterest = faPinterest;
  protected readonly faYoutube = faYoutube;
  protected readonly faInstagram = faInstagram;
  protected readonly faReddit = faReddit;
  protected readonly faCartShopping = faCartShopping;
  protected readonly faHeart = faHeart;
  protected readonly faUser = faUser;
  protected readonly search = faSearch;
  protected readonly faLocationDot = faLocationDot;
  protected readonly faRotate = faRotate;
  protected readonly faHeadset = faHeadset;
  protected readonly faCircleInfo = faCircleInfo;
  protected readonly faPhoneVolume = faPhoneVolume;
}
