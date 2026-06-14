import { Directive, ElementRef, AfterViewInit } from '@angular/core';

@Directive({
    selector: 'p-editor',
    standalone: false
})
export class PrimengCompatDirective implements AfterViewInit {
  constructor(private readonly el: ElementRef) {}

  ngAfterViewInit() {
    const content = (this.el.nativeElement as HTMLElement).querySelector('.p-editor-content');
    if (content) {
      content.classList.add('ui-editor-content');
    }
  }
}
