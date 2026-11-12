import {Component, computed, inject, signal} from '@angular/core';
import {applyEach, form, FormField, FormRoot, min, pattern, required} from '@angular/forms/signals';
import {NeueBestellung, Position} from '../../modell';
import {BestellService, summe} from '../../bestell-service';
import {CurrencyPipe} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
import {fehlertext, istProblem} from '../../fehlertext';
import {HttpErrorResponse} from '@angular/common/http';

const leerePosition = (): Position => ({artikel: '', menge: 1, einzelpreis: 0});

@Component({
  imports: [FormField, CurrencyPipe, FormRoot, RouterLink],
  selector: 'app-bestell-formular',
  styleUrl: './bestell-formular.css',
  templateUrl: './bestell-formular.html',
})
export class BestellFormular {
  private readonly service = inject(BestellService);
  private readonly router = inject(Router);
  protected readonly daten = signal<NeueBestellung>({kunde: '', positionen: [leerePosition()]});
  protected readonly formular = form(this.daten, (pfad) => {
      required(pfad.kunde, {message: 'Bitte einen Kunden angeben'});
      pattern(pfad.kunde, /\S/, {message: 'Nur Leerzeichen sind kein Name'});
      applyEach(pfad.positionen, (position) => {
        required(position.artikel, {message: 'Artikel fehlt'});
        required(position.menge, {message: 'Menge fehlt'});
        required(position.einzelpreis, {message: 'Preis fehlt'});
        min(position.menge, 1, {message: 'Menge mindestens 1'});
        min(position.einzelpreis, 0.01, {message: 'Preis muss größer als 0 sein'});
      });
    },
    {
      submission: {
        action: async (feld) => {
          try {
            const neu = await this.service.anlegen(feld().value());
            await this.router.navigate(['/bestellungen', neu.id]);
            return undefined;
          } catch (e) {
            if (e instanceof HttpErrorResponse && istProblem(e.error) && e.error.fehler) {
              return e.error.fehler.map((f) => ({
                kind: 'server',
                message: f.meldung,
                fieldTree: f.feld === 'kunde' ? feld.kunde : feld,
              }));
            }
            return {kind: 'server', message: fehlertext(e)};
          }
        },
      },
    },
  );

  protected readonly gesamt = computed(() => summe(this.daten()));

  protected positionHinzufuegen() {
    this.daten.update((d) => ({...d, positionen: [...d.positionen, leerePosition()]}));
  }

  protected positionEntfernen(index: number): void {
    this.daten.update((d) => ({...d, positionen: d.positionen.filter((_, i) => i !== index)}));
  }


}

