import {Component, input, model} from '@angular/core';
import {Status, STATUS_WERTE} from '../modell';

export type StatusAuswahl = Status | 'alle';

@Component({
  imports: [],
  selector: 'app-status-filter',
  styleUrl: './status-filter.css',
  templateUrl: './status-filter.html',
})
export class StatusFilter {

  readonly auswahl = model<StatusAuswahl>('alle');

  readonly anzahl = input.required<Record<StatusAuswahl, number>>();

  protected readonly optionen: StatusAuswahl[] = [
    'alle', ...STATUS_WERTE
  ];
}
