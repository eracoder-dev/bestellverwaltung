import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'positionen',
})
export class PositionenPipe implements PipeTransform {
  transform(anzahl: number): string {
    if (anzahl === 1) {
      return '1 Position';
    } else {
      return `${anzahl} Positionen`;
    }
  }
}
