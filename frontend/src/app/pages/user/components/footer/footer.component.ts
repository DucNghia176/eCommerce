import {Component} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {
  faApple,
  faAppStore,
  faAppStoreIos,
  faFacebook,
  faGooglePlay,
  faInstagram,
  faPinterest,
  faReddit,
  faTwitter,
  faYoutube
} from "@fortawesome/free-brands-svg-icons";

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [
    FaIconComponent
  ],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.scss'
})
export class FooterComponent {

  protected readonly faGooglePlay = faGooglePlay;
  protected readonly faAppStore = faAppStore;
  protected readonly faAppStoreIos = faAppStoreIos;
  protected readonly faApple = faApple;
  protected readonly faInstagram = faInstagram;
  protected readonly faTwitter = faTwitter;
  protected readonly faFacebook = faFacebook;
  protected readonly faYoutube = faYoutube;
  protected readonly faPinterest = faPinterest;
  protected readonly faReddit = faReddit;
}
