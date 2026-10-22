import {Component, input} from '@angular/core';
import {Status} from '../modell';

@Component({
  imports: [],
  selector: 'app-status-badge',
  styleUrl: './status-badge.css',
  templateUrl: './status-badge.html',
})
export class StatusBadge {
  readonly status = input.required<Status>();
}
