import {Component, computed, inject, input, linkedSignal, model, numberAttribute, signal} from '@angular/core';
import {RouterLink} from '@angular/router';
import {BestellService, summe} from '../bestell-service';
import {StatusBadge} from '../status-badge/status-badge';
import {CurrencyPipe} from '@angular/common';
import {Status, STATUS_WERTE} from '../modell';
import {MengeAendern} from '../menge-aendern/menge-aendern';

@Component({
  imports: [RouterLink, StatusBadge, CurrencyPipe, MengeAendern],
  selector: 'app-bestell-detail',
  styleUrl: './bestell-detail.css',
  templateUrl: './bestell-detail.html',
})
export class BestellDetail {
  readonly id = input.required({transform: numberAttribute});
  readonly positionenSichtbar = signal(true);
  protected speichert = signal(false);
  protected readonly stornoFragen = linkedSignal({source: this.id, computation: () => false});
  protected readonly summe = summe;
  protected readonly model = model;
  protected readonly statusWerte: Status[] = [...STATUS_WERTE];
  private readonly service = inject(BestellService);
  readonly bestellung = computed(() => this.service.alle().find((b) => b.id === this.id()));
  private readonly index = computed(() => this.service.alle().findIndex((b) => b.id === this.id()));
  protected vorherigeBestellung = computed(() => {
    if (this.index() <= 0) {
      return undefined;
    }
    return this.service.alle().at(this.index() - 1);
  });
  protected naechsteBestellung = computed(() => this.service.alle().at(this.index() + 1));

  protected warte = (ms: number) => new Promise<void>((resolve) => setTimeout(resolve, ms));

  protected async statusSetzen(status: Status): Promise<void> {
    try {
      this.speichert.set(true);
      await this.service.statusSetzen(this.id(), status);
    } finally {
      this.speichert.set(false);
    }
  }

  protected waehlen(status: Status): void {
    if (status === 'storniert') {
      this.stornoFragen.set(true);
    } else {
      this.statusSetzen(status);
    }
  }

  protected switchPos() {
    this.positionenSichtbar.update((p) => !p);
  }

  protected async mengeSetzen(artikel: string, menge: number): Promise<void> {
    this.speichert.set(true);
    try {
      await this.service.mengeSetzen(this.id(), artikel, menge);
    } finally {
      this.speichert.set(false);
    }
  }
}
