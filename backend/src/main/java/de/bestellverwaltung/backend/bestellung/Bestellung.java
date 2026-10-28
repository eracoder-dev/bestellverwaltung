package de.bestellverwaltung.backend.bestellung;

import java.util.ArrayList;
import java.util.List;

public record Bestellung(long id, String kunde, List<Position> positionen, Status status) {

    public Bestellung mitStatus(Status neuerStatus) {
        return new Bestellung(id, kunde, positionen, neuerStatus);
    }

    public Bestellung mitMenge(int nr, int menge) {
        List<Position> neu = new ArrayList<>(this.positionen);
        Position alt = neu.get(nr - 1);
        neu.set(nr - 1, new Position(alt.artikel(), menge, alt.einzelpreis()));
        return new Bestellung(id, kunde, List.copyOf(neu), status);
    }
}
