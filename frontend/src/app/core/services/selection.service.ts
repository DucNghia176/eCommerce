import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SelectionService<T> {
  selectedItems: T[] = [];
  isAllSelected = false;

  constructor() {
  }

  toggleSelection(id: T, checked: boolean, all: T[]) {
    if (checked) {
      if (!this.selectedItems.includes(id)) {
        this.selectedItems.push(id);
      }

    } else {
      this.selectedItems = this.selectedItems.filter(item => item !== id);
    }
    this.isAllSelected = this.selectedItems.length === all.length;
  }

  toggleAll(checked: boolean, all: T[]) {
    this.isAllSelected = checked;
    if (checked) {
      this.selectedItems = [...all];
    } else {
      this.selectedItems = [];
    }
  }

  getSelectedItems(): T[] {
    return this.selectedItems;
  }

  clearSelection() {
    this.selectedItems = [];
    this.isAllSelected = false;
  }
}
