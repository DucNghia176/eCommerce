import {Component, ElementRef, ViewChild} from '@angular/core';
import {NgForOf} from "@angular/common";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faArrowLeft, faArrowRight} from "@fortawesome/free-solid-svg-icons";

export interface ShopWithCategory {
  id: number;
  name: string;
  image: string;
}

@Component({
  selector: 'app-shop-with-category',
  standalone: true,
  imports: [
    NgForOf,
    FaIconComponent
  ],
  templateUrl: './shop-with-category.component.html',
  styleUrl: './shop-with-category.component.scss'
})
export class ShopWithCategoryComponent {
  @ViewChild('scrollContainer', {static: true}) scrollContainer!: ElementRef;

  categories = [
    {
      id: 1,
      name: 'Electronics',
      image: 'https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg'
    },
    {
      id: 2,
      name: 'Fashion',
      image: 'https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg'
    },
    {
      id: 3,
      name: 'Home',
      image: 'https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg'
    },
    {
      id: 4,
      name: 'Books',
      image: 'https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg'
    },
    {
      id: 5,
      name: 'Shoes',
      image: 'https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg'
    },
    {
      id: 6,
      name: 'Toys',
      image: 'https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg'
    },
    {
      id: 7,
      name: 'Sports',
      image: 'https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg'
    },
    {
      id: 8,
      name: 'Beauty',
      image: 'https://res.cloudinary.com/dukqrda6g/image/upload/product/2/otkl8kxgl3byzklyhxxp.jpg'
    },
  ];
  protected readonly faArrowRight = faArrowRight;
  protected readonly faArrowLeft = faArrowLeft;

  scrollLeft() {
    const container = this.scrollContainer.nativeElement;
    const newPos = container.scrollLeft - 200;
    container.scrollTo({left: newPos, behavior: 'smooth'});
  }

  scrollRight() {
    const container = this.scrollContainer.nativeElement;
    const newPos = container.scrollLeft + 200;
    container.scrollTo({left: newPos, behavior: 'smooth'});
  }


  private animateScroll(target: number, duration: number = 500) {
    const container = this.scrollContainer.nativeElement;
    const start = container.scrollLeft;
    const change = target - start;
    const startTime = performance.now();

    const animate = (currentTime: number) => {
      const elapsed = currentTime - startTime;
      const progress = Math.min(elapsed / duration, 1);

      // easing (easeInOutQuad)
      const ease = progress < 0.5
        ? 2 * progress * progress
        : -1 + (4 - 2 * progress) * progress;

      container.scrollLeft = start + change * ease;

      if (elapsed < duration) {
        requestAnimationFrame(animate);
      }
    };

    requestAnimationFrame(animate);
  }
}
