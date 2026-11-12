package de.bestellverwaltung.backend.bestellung;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Entity
@Table(name = "bestellung")
public class BestellungEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kunde;

    private Instant angelegtAm;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ElementCollection
    @CollectionTable(name = "position", joinColumns = @JoinColumn(name = "bestellung_id"))
    @OrderColumn(name = "nr")
    private List<Position> positionen = new ArrayList<>();

    protected BestellungEntity() {
    }

    public BestellungEntity(String kunde, List<Position> positionen, Status status) {
        this.kunde = kunde;
        this.positionen = new ArrayList<>(positionen);
        this.status = status;
        this.angelegtAm = Instant.now();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Bestellung alsBestellung() {
        return new Bestellung(id, kunde, List.copyOf(positionen), status, angelegtAm);
    }

    public void mengeSetzen(int nr, int menge) {
        if (nr < 1 || nr > positionen.size()) {
            throw new NoSuchElementException("Bestellung " + id + " hat keine Position " + nr);
        }
        Position alt = positionen.get(nr - 1);
        positionen.set(nr - 1, new Position(alt.artikel(), menge, alt.einzelpreis()));
    }

    public Instant getAngelegtAm() {
        return angelegtAm;
    }

    public void setAngelegtAm(Instant angelegtAm) {
        this.angelegtAm = angelegtAm;
    }
}
