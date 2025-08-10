import {Component, OnInit} from '@angular/core';
import {CommonModule} from "@angular/common";
import {ToastItem, ToastService} from "../../../core/services/toast.service";
import {faClose} from "@fortawesome/free-solid-svg-icons";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule, FaIconComponent],
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.scss'
})
export class ToastComponent implements OnInit {
  toasts: ToastItem[] = [];
  protected readonly faClose = faClose;

  constructor(private toastService: ToastService) {
  }

  ngOnInit() {
    this.toastService.toasts$.subscribe(data => this.toasts = data);
  }

  closeToast(id: number): void {
    this.toastService.removeToast(id);
  }
}
