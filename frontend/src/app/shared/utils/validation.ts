import {ElementRef} from '@angular/core';
import {NgForm} from '@angular/forms';

export function validateAndFocusFirstError(form: NgForm, formRef: ElementRef): boolean {
  if (form.invalid) {
    // Đánh dấu tất cả trường là touched để hiển thị lỗi
    Object.values(form.controls).forEach(control => control.markAsTouched());

    // Tìm tên trường đầu tiên bị lỗi
    const firstInvalidControlName = Object.keys(form.controls).find(key => form.controls[key].invalid);

    // Tìm input tương ứng và focus
    if (firstInvalidControlName) {
      const invalidElement = formRef.nativeElement.querySelector(`[name="${firstInvalidControlName}"]`);
      if (invalidElement) {
        invalidElement.focus();
      }
    }

    return false; // Form không hợp lệ
  }

  return true; // Form hợp lệ
}
