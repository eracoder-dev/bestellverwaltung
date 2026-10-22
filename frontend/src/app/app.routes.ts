import { Routes } from '@angular/router';
import { BestellDetail } from './bestellungen/bestell-detail/bestell-detail';
import { BestellListe } from './bestellungen/bestell-liste/bestell-liste';
import { NichtGefunden } from './nicht-gefunden/nicht-gefunden';

export const routes: Routes = [
  { path: '', component: BestellListe, title: 'Bestellungen' },
  { path: 'bestellungen/:id', component: BestellDetail, title: 'Bestellung' },
  { path: '**', component: NichtGefunden, title: 'Seite nicht gefunden' },
];
