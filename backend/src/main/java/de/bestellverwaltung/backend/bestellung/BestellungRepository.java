package de.bestellverwaltung.backend.bestellung;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BestellungRepository extends JpaRepository<BestellungEntity, Long> {

    @Override
    @EntityGraph(attributePaths = "positionen")
    List<BestellungEntity> findAll(Sort sort);
}
