import {SidebarComponent} from "../../components/sidebar/sidebar.component";
import {NavbarComponent} from "../../components/navbar/navbar.component";
import {CommonModule} from "@angular/common";
import {Component} from "@angular/core";
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [SidebarComponent, NavbarComponent, CommonModule, RouterOutlet],
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.scss'
})
export class AdminLayoutComponent {

}
