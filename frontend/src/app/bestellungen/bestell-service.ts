import {computed, inject, Service} from '@angular/core';
import {Bestellung, NeueBestellung, Status} from './modell';
import {HttpClient, httpResource} from '@angular/common/http';
import {firstValueFrom} from 'rxjs';

const API = '/api/bestellungen';

@Service()
export class BestellService {
  private readonly bestellungen = httpResource<Bestellung[]>(() => API, {
    defaultValue: [],
  });
  private readonly http = inject(HttpClient);
  readonly laedt = this.bestellungen.isLoading;
  readonly fehler = this.bestellungen.error;
  readonly alle = computed(() => this.bestellungen.hasValue() ? this.bestellungen.value() : []);
  readonly umsatz = computed(() =>
    this.alle()
      .filter((b) => b.status !== 'storniert')
      .reduce((s, b) => s + summe(b), 0),
  );

  reload() {
    this.bestellungen.reload();
  }

  async mengeSetzen(id: number, nr: number, menge: number): Promise<Bestellung> {

    const geaendert = await firstValueFrom(
      this.http.put<Bestellung>(`${API}/${id}/positionen/${nr}`, {menge})
    );
    this.ersetzen(geaendert);
    return geaendert;
  }

  async statusSetzen(id: number, status: Status): Promise<Bestellung> {
    const geaendert = await firstValueFrom(
      this.http.put<Bestellung>(`${API}/${id}/status`, {status}),
    );
    this.ersetzen(geaendert);
    return geaendert;
  }

  async anlegen(daten: NeueBestellung): Promise<Bestellung> {
    const neu = await firstValueFrom(this.http.post<Bestellung>(API, daten));
    this.bestellungen.update((liste) => [...liste, neu]);
    return neu;
  }

  private ersetzen(geaendert: Bestellung): void {
    this.bestellungen.update((liste) => liste.map((b) => (b.id === geaendert.id ? geaendert : b)));
  }
}

export function summe(b: Pick<Bestellung, 'positionen'>): number {
  return b.positionen.reduce((s, p) => s + p.menge * p.einzelpreis, 0);
}
