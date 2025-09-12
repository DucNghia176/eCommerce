import {Component} from '@angular/core';
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faFacebook, faInstagram, faPinterest, faReddit, faTwitter, faYoutube} from "@fortawesome/free-brands-svg-icons";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    FaIconComponent
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

  protected readonly faTwitter = faTwitter;
  protected readonly faFacebook = faFacebook;
  protected readonly faPinterest = faPinterest;
  protected readonly faYoutube = faYoutube;
  protected readonly faInstagram = faInstagram;
  protected readonly faReddit = faReddit;
}
