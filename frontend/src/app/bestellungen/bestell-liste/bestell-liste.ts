import {CurrencyPipe, PercentPipe} from '@angular/common';
import {Component, computed, effect, inject, signal} from '@angular/core';
import {BestellService, summe} from '../bestell-service';
import {StatusBadge} from '../status-badge/status-badge';
import {StatusAuswahl, StatusFilter} from '../status-filter/status-filter';
import {RouterLink} from '@angular/router';
import {Kennzahl} from '../kennzahl/kennzahl';
import {Suchfeld} from '../suchfeld/suchfeld';
import {STATUS_WERTE} from '../modell';
import {PositionenPipe} from '../positionen-pipe';

@Component({
  imports: [
    CurrencyPipe,
    StatusBadge,
    StatusFilter,
    RouterLink,
    Kennzahl,
    Suchfeld,
    PercentPipe,
    PositionenPipe,
  ],
  selector: 'app-bestell-liste',
  styleUrl: './bestell-liste.css',
  templateUrl: './bestell-liste.html',
})
export class BestellListe {
  protected readonly suche = signal('');
  protected readonly filter = signal<StatusAuswahl>(this.gespeicherterFilter());
  protected readonly summe = summe;
  protected nachSumme = signal<boolean>(false);
  private readonly service = inject(BestellService);
  protected readonly bestellungen = computed(() => {
    const auswahl = this.filter();
    const suche = this.suche().toLowerCase();
    const gefiltert = this.service
      .alle()
      .filter((b) => auswahl === 'alle' || b.status === auswahl)
      .filter((b) => b.kunde.toLowerCase().includes(suche));

    return this.nachSumme() ? gefiltert.toSorted((a, b) => summe(b) - summe(a)) : gefiltert;
  });
  protected readonly anzahl = computed(() => {
    const zaehler: Record<StatusAuswahl, number> = {
      alle: 0,
      offen: 0,
      bezahlt: 0,
      versandt: 0,
      storniert: 0,
    };

    for (const item of this.service.alle()) {
      zaehler['alle']++;
      zaehler[item.status]++;
    }
    return zaehler;
  });
  protected readonly gesamt = computed(() => this.service.alle().length);
  protected readonly umsatz = this.service.umsatz;

  constructor() {
    effect(() => {
      localStorage.setItem('bestellungen.filter', this.filter());
    });
  }

  gespeicherterFilter(): StatusAuswahl {
    let myFilter = localStorage.getItem('bestellungen.filter');
    if (this.istStatusAuswahl(myFilter)) {
      return myFilter;
    }
    return 'alle';
  }

  istStatusAuswahl(wert: string | null): wert is StatusAuswahl {
    return wert !== null && (STATUS_WERTE as readonly string[]).includes(wert);
  }

  protected changeSort() {
    this.nachSumme.set(!this.nachSumme());
  }
}
