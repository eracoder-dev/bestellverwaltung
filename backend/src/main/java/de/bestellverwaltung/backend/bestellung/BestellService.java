package de.bestellverwaltung.backend.bestellung;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class BestellService {

    private final Map<Long, Bestellung> bestellungen = new ConcurrentSkipListMap<>();
    private final AtomicLong naechsteId = new AtomicLong(1);

    public BestellService() {
        speichere("Ada", Status.OFFEN, List.of(new Position("Tastatur", 1, new BigDecimal("49.90"))));
        speichere("Linus",
                  Status.BEZAHLT,
                  List.of(new Position("Monitor", 2, new BigDecimal("199.00")),
                          new Position("Kabel", 3, new BigDecimal("4.50"))));
        speichere("Grace", Status.STORNIERT, List.of(new Position("Maus", 1, new BigDecimal("25.00"))));
        speichere("Margaret", Status.VERSANDT, List.of(new Position("Headset", 1, new BigDecimal("79.00"))));
    }

    public List<Bestellung> alle() {
        return List.copyOf(bestellungen.values());
    }

    public Optional<Bestellung> finde(long id) {
        return Optional.ofNullable(bestellungen.get(id));
    }

    public Bestellung anlegen(NeueBestellung daten) {
        return speichere(daten.kunde().strip(), Status.OFFEN, daten.positionen());
    }

    public Optional<Bestellung> mengeSetzen(long id, int nr, int menge) {
        return finde(id).map(vorhanden -> {
            if (vorhanden.status() != Status.OFFEN) {
                throw new IllegalStateException("Bestellung " + id + " ist nicht mehr offen");
            }
            if (nr < 1 || nr > vorhanden.positionen().size()) {
                throw new NoSuchElementException("Bestellung " + id + " hat keine Position " + nr);
            }

            Bestellung geaendert = vorhanden.mitMenge(nr, menge);
            bestellungen.put(id, geaendert);
            return geaendert;
        });
    }

    public Optional<Bestellung> statusSetzen(long id, Status status) {
        return finde(id).map(vorhanden -> {
            if (vorhanden.status() == Status.STORNIERT) {
                throw new IllegalStateException("Bestellung " + id + " ist storniert");
            }
            Bestellung geaendert = vorhanden.mitStatus(status);
            bestellungen.put(id, geaendert);
            return geaendert;
        });
    }

    private Bestellung speichere(String kunde, Status status, List<Position> positionen) {
        Bestellung neu = new Bestellung(naechsteId.getAndIncrement(), kunde, List.copyOf(positionen), status);
        bestellungen.put(neu.id(), neu);
        return neu;
    }
}