import {SidebarComponent} from "../../components/layout/sidebar/sidebar.component";
import {NavbarComponent} from "../../components/layout/navbar/navbar.component";
import {CommonModule} from "@angular/common";
import {Component} from "@angular/core";
import {RouterOutlet} from "@angular/router";
import {ToastComponent} from "../../../../shared/components/toast/toast.component";

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [SidebarComponent, NavbarComponent, CommonModule, RouterOutlet, ToastComponent],
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.scss'
})
export class AdminLayoutComponent {

}
