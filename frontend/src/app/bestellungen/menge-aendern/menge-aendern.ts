import {Component, input, output} from '@angular/core';

@Component({
  imports: [],
  selector: 'app-menge-aendern',
  styleUrl: './menge-aendern.css',
  templateUrl: './menge-aendern.html',
})
export class MengeAendern {
  readonly menge = input.required<number>();
  readonly gesperrt = input(false);
  readonly aendern = output<number>();
}
