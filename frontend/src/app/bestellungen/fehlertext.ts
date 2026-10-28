import {HttpErrorResponse} from '@angular/common/http';

export interface Problem {
  title: string;
  status: number;
  detail?: string;
  fehler?: { feld: string; meldung: string }[];
}

export function istProblem(x: unknown): x is Problem {
  return typeof x === 'object' && x !== null && 'title' in x && 'status' in x;
}

export function fehlertext(e: unknown): string {
  if (e instanceof HttpErrorResponse) {
    if (istProblem(e.error)) {
      const meldungen = e.error.fehler?.map((f) => f.meldung) ?? [];
      return meldungen.length > 0 ? meldungen.join(', ') : (e.error.detail ?? e.error.title);
    }
    return e.status === 0 ? 'Server nicht erreichbar' : `Fehler ${e.status}`;
  }
  return e instanceof Error ? e.message : String(e);
}
