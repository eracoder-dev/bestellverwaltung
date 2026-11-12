package de.bestellverwaltung.backend.bestellung;

import java.time.Instant;
import java.util.List;

public record Bestellung(long id, String kunde, List<Position> positionen, Status status, Instant angelegtAm) {

}
