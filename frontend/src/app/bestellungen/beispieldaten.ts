import { Bestellung } from './modell';

export const BEISPIELDATEN: Bestellung[] = [
  {
    id: 1,
    kunde: 'Ada',
    status: 'offen',
    positionen: [{ artikel: 'Tastatur', menge: 1, einzelpreis: 49.9 }],
  },
  {
    id: 2,
    kunde: 'Linus',
    status: 'bezahlt',
    positionen: [
      { artikel: 'Monitor', menge: 2, einzelpreis: 199 },
      { artikel: 'Kabel', menge: 3, einzelpreis: 4.5 },
    ],
  },
  {
    id: 3,
    kunde: 'Grace',
    status: 'storniert',
    positionen: [{ artikel: 'Maus', menge: 1, einzelpreis: 25 }],
  },
  {
    id: 4,
    kunde: 'Margaret',
    status: 'versandt',
    positionen: [{ artikel: 'Headset', menge: 1, einzelpreis: 79 }],
  },
];
