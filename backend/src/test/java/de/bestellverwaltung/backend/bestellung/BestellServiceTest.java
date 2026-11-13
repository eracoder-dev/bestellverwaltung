package de.bestellverwaltung.backend.bestellung;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BestellServiceTest {

    @Mock
    private BestellungRepository repository;

    @InjectMocks
    private BestellService service;

    private static BestellungEntity bestellung(long id, Status status) {
        BestellungEntity b = new BestellungEntity(
                "Grace", List.of(new Position("Maus", 1, new BigDecimal("25.00"))), status);
        ReflectionTestUtils.setField(b, "id", id);
        return b;
    }

    @Test
    void statusSetzenAendertDenStatus() {
        when(repository.findById(1L)).thenReturn(Optional.of(bestellung(1, Status.OFFEN)));

        Optional<Bestellung> ergebnis = service.statusSetzen(1, Status.BEZAHLT);

        assertThat(ergebnis).hasValueSatisfying(b -> assertThat(b.status()).isEqualTo(Status.BEZAHLT));
    }

    @Test
    void stornierteBestellungLaesstSichNichtAendern() {
        when(repository.findById(3L)).thenReturn(Optional.of(bestellung(3, Status.STORNIERT)));

        assertThatThrownBy(() -> service.statusSetzen(3, Status.BEZAHLT))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Bestellung 3 ist storniert");
    }

    @Test
    void unbekannteBestellungErgibtEinLeeresOptional() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThat(service.statusSetzen(99, Status.BEZAHLT)).isEmpty();
    }
}
