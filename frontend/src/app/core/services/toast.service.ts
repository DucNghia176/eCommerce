import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";

export interface ToastItem {
  message: string;
  type: 'p' | 'f'; // p = success, f = fail
  id: number;
}

@Injectable({
  providedIn: 'root'
})


export class ToastService {

  private toasts: ToastItem[] = [];
  private toastSubject = new BehaviorSubject<ToastItem[]>([]);
  toasts$ = this.toastSubject.asObservable();

  show(message: string, type: 'p' | 'f' = 'p') {
    const id = Date.now(); // ID duy nhất

    const toast: ToastItem = {message, type, id};
    this.toasts.push(toast);
    this.toastSubject.next(this.toasts);

    setTimeout(() => {
      this.toasts = this.toasts.filter(t => t.id !== id);
      this.toastSubject.next(this.toasts);
    }, 3000);
  }

  removeToast(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
    this.toastSubject.next(this.toasts);
  }

}
