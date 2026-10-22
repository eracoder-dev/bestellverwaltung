export const STATUS_WERTE = ['offen', 'bezahlt', 'versandt', 'storniert'] as const;
export type Status = (typeof STATUS_WERTE)[number];
export interface Position { artikel: string, menge: number, einzelpreis: number }
export interface Bestellung {
  id: number;
  kunde: string;
  positionen: Position[];
  status: Status;
}
export type NeueBestellung = Omit<Bestellung, 'id' | 'status'>;
