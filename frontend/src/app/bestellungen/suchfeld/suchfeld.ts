import {Component, ElementRef, model, viewChild} from '@angular/core';
import {Fokus} from '../fokus';

@Component({
  imports: [Fokus],
  selector: 'app-suchfeld',
  styleUrl: './suchfeld.css',
  templateUrl: './suchfeld.html',
})
export class Suchfeld {
  readonly text = model('');
  private readonly feld = viewChild.required<ElementRef<HTMLInputElement>>('feld');

  protected leeren() {
    this.text.set('');
    this.feld().nativeElement.focus();
  }
}
