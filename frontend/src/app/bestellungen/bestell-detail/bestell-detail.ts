import {Component, computed, inject, input, linkedSignal, model, numberAttribute, signal} from '@angular/core';
import {RouterLink} from '@angular/router';
import {BestellService, summe} from '../bestell-service';
import {StatusBadge} from '../status-badge/status-badge';
import {CurrencyPipe} from '@angular/common';
import {Status, STATUS_WERTE} from '../modell';
import {MengeAendern} from '../menge-aendern/menge-aendern';
import {fehlertext} from '../fehlertext';

@Component({
  imports: [RouterLink, StatusBadge, CurrencyPipe, MengeAendern],
  selector: 'app-bestell-detail',
  styleUrl: './bestell-detail.css',
  templateUrl: './bestell-detail.html',
})
export class BestellDetail {
  readonly id = input.required({transform: numberAttribute});
  readonly positionenSichtbar = signal(true);
  protected readonly fehler = signal('');
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

  protected readonly laedt = this.service.laedt;

  protected async statusSetzen(status: Status): Promise<void> {
    this.speichert.set(true);
    this.fehler.set('');
    try {
      await this.service.statusSetzen(this.id(), status);
    } catch (e) {
      this.fehler.set(fehlertext(e));
      await this.service.reload();
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

  protected async mengeSetzen(index: number, menge: number): Promise<void> {
    this.speichert.set(true);
    try {
      await this.service.mengeSetzen(this.id(), index, menge);
    } catch (e) {
      this.fehler.set(fehlertext(e));
      this.service.reload();
    } finally {
      this.speichert.set(false);
    }
  }
}
