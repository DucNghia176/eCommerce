import {Directive, ElementRef, Input} from '@angular/core';

@Directive({
  selector: '[appStatus]',
  standalone: true
})
export class StatusDirective {

  constructor(private el: ElementRef) {
  }

  @Input() set appStatus(meta: { label?: string; color: string; bgColor?: string }) {
    if (meta.label) {
      this.el.nativeElement.textContent = meta.label;
    }
    this.el.nativeElement.style.color = meta.color;
    this.el.nativeElement.style.backgroundColor = meta.bgColor ?? '#f3f4f6';
    this.el.nativeElement.style.padding = '2px 8px';
    this.el.nativeElement.style.borderRadius = '8px';
  }
}
