import {Injectable} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {Observable} from "rxjs";
import {ConfirmDialogComponent} from "../../shared/components/confirm-dialog/confirm-dialog.component";

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(private dialog: MatDialog) {
  }

  confirmAdd(message: string): Observable<boolean> {
    return this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        type: 'add',
        title: 'Lưu thông tin',
        message
      }
    }).afterClosed();
  }

  confirmDelete(message: string): Observable<boolean> {
    return this.dialog.open(ConfirmDialogComponent, {
      width: 'w-80',
      data: {
        type: 'delete',
        title: 'Xóa thông tin',
        message
      }
    }).afterClosed();
  }
}
