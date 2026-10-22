import {computed, Service} from '@angular/core';
import {Bestellung, Status} from './modell';
import {httpResource} from '@angular/common/http';

const API = '/api/bestellungen';
const warte = (ms: number) => new Promise<void>((resolve) => setTimeout(resolve, ms));

@Service()
export class BestellService {
  private readonly bestellungen = httpResource<Bestellung[]>(() => API, {
    defaultValue: [],
  });
  readonly laedt = this.bestellungen.isLoading;
  readonly fehler = this.bestellungen.error;
  readonly alle = computed(() => this.bestellungen.hasValue() ? this.bestellungen.value() : []);
  readonly umsatz = computed(() =>
    this.alle()
      .filter((b) => b.status !== 'storniert')
      .reduce((s, b) => s + summe(b), 0),
  );

  async reload() {
    this.bestellungen.reload();
  }

  async mengeSetzen(id: number, artikel: string, menge: number): Promise<Bestellung> {
    await warte(300);
    const vorhanden = this.alle().find((b) => b.id === id);
    if (!vorhanden) {
      throw new Error(`Bestellung ${id} nicht gefunden`);
    }
    if (vorhanden.status !== 'offen') {
      throw new Error(`Bestellung ${id} ist nicht mehr offen`);
    }
    if (menge < 1) {
      throw new Error(`Menge muss größer 0 sein.`);
    }
    const geaendert = {
      ...vorhanden,
      positionen: vorhanden.positionen.map((p) => (p.artikel === artikel ? {...p, menge} : p)),
    };
    this.bestellungen.update((liste) => liste.map((b) => (b.id === id ? geaendert : b)));
    return geaendert;
  }

  async statusSetzen(id: number, status: Status): Promise<Bestellung> {
    await warte(300);
    const vorhanden = this.alle().find((b) => b.id === id);
    if (!vorhanden) {
      throw new Error(`Bestellung ${id} nicht gefunden`);
    }
    if (vorhanden.status === 'storniert') {
      throw new Error(`Bestellung ${id} ist storniert`);
    }
    const geaendert = {...vorhanden, status};
    this.bestellungen.update((liste) => liste.map((b) => (b.id === id ? geaendert : b)));
    return geaendert;
  }
}

export function summe(b: Bestellung): number {
  return b.positionen.reduce((s, p) => s + p.menge * p.einzelpreis, 0);
}
