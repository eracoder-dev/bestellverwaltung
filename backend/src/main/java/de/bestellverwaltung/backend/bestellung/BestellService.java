package de.bestellverwaltung.backend.bestellung;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BestellService {

    private final Map<Long, Bestellung> bestellungen = new ConcurrentSkipListMap<>();
    private final AtomicLong naechsteId = new AtomicLong(1);
    private final BestellungRepository repository;

    public BestellService(BestellungRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Bestellung> alle() {
        return repository.findAll(Sort.by("id")).stream().map(BestellungEntity::alsBestellung).toList();
    }

    @Transactional(readOnly = true)
    public Optional<Bestellung> finde(long id) {
        return repository.findById(id).map(BestellungEntity::alsBestellung);
    }

    public Bestellung anlegen(NeueBestellung daten) {
        BestellungEntity neu = new BestellungEntity(daten.kunde().strip(), daten.positionen(), Status.OFFEN);
        return repository.save(neu).alsBestellung();
    }

    public Optional<Bestellung> mengeSetzen(long id, int nr, int menge) {
        return repository.findById(id).map(vorhanden -> {
            if (vorhanden.getStatus() != Status.OFFEN) {
                throw new IllegalStateException("Bestellung " + id + " ist nicht mehr offen");
            }
            vorhanden.mengeSetzen(nr, menge);
            return vorhanden.alsBestellung();
        });
    }

    public Optional<Bestellung> statusSetzen(long id, Status status) {
        return repository.findById(id).map(vorhanden -> {
            if (vorhanden.getStatus() == Status.STORNIERT) {
                throw new IllegalStateException("Bestellung " + id + " ist storniert");
            }
            vorhanden.setStatus(status);
            return vorhanden.alsBestellung();
        });
    }
}