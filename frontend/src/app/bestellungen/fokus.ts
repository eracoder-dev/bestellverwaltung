import {afterNextRender, Directive, ElementRef, inject} from '@angular/core';

@Directive({
  selector: '[appFokus]',
})
export class Fokus {
  private readonly appFokus = inject<ElementRef<HTMLInputElement>>(ElementRef);

  constructor() {
    afterNextRender(() => {
      this.appFokus.nativeElement.focus();
    });
  }
}
