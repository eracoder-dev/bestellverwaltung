import { Service, computed, signal } from '@angular/core';
import { BEISPIELDATEN } from './beispieldaten';
import { Bestellung, NeueBestellung, Status } from './modell';

const warte = (ms: number) => new Promise<void>((resolve) => setTimeout(resolve, ms));

@Service()
export class BestellService {
  private readonly bestellungen = signal<Bestellung[]>(BEISPIELDATEN);
  private naechsteId = BEISPIELDATEN.length + 1;

  // Von außen nur lesbar: wer ändern will, muss die Methoden benutzen
  readonly alle = this.bestellungen.asReadonly();

  readonly umsatz = computed(() =>
    this.bestellungen()
      .filter((b) => b.status !== 'storniert')
      .reduce((s, b) => s + summe(b), 0),
  );

  async anlegen(daten: NeueBestellung): Promise<Bestellung> {
    await warte(100);
    const neu: Bestellung = { ...daten, id: this.naechsteId++, status: 'offen' };
    this.bestellungen.update((liste) => [...liste, neu]);
    return neu;
  }

  async mengeSetzen(id: number, artikel: string, menge: number): Promise<Bestellung> {
    await warte(100);
    const vorhanden = this.bestellungen().find((b) => b.id === id);
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
      positionen: vorhanden.positionen.map((p) => (p.artikel === artikel ? { ...p, menge } : p)),
    };
    this.bestellungen.update((liste) => liste.map((b) => (b.id === id ? geaendert : b)));
    return geaendert;
  }

  async statusSetzen(id: number, status: Status): Promise<Bestellung> {
    await warte(1000);
    const vorhanden = this.bestellungen().find((b) => b.id === id);
    if (!vorhanden) {
      throw new Error(`Bestellung ${id} nicht gefunden`);
    }
    if (vorhanden.status === 'storniert') {
      throw new Error(`Bestellung ${id} ist storniert`);
    }
    const geaendert = { ...vorhanden, status };
    this.bestellungen.update((liste) => liste.map((b) => (b.id === id ? geaendert : b)));
    return geaendert;
  }
}

export function summe(b: Bestellung): number {
  return b.positionen.reduce((s, p) => s + p.menge * p.einzelpreis, 0);
}
