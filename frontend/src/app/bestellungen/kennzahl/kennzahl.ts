import { Component, input } from '@angular/core';

@Component({
  imports: [],
  selector: 'app-kennzahl',
  styleUrl: './kennzahl.css',
  templateUrl: './kennzahl.html',
})
export class Kennzahl {
  titel = input.required<string>();
}

