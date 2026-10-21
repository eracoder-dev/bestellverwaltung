package de.bestellverwaltung.backend.bestellung;

import java.util.List;

public record Bestellung(long id, String kunde, List<Position> positionen, Status status) {

    public Bestellung mitStatus(Status neuerStatus) {
        return new Bestellung(id, kunde, positionen, neuerStatus);
    }
}
