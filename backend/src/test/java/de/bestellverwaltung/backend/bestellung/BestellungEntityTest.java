package de.bestellverwaltung.backend.bestellung;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class BestellungEntityTest {

    private static BestellungEntity linus() {
        BestellungEntity linus = new BestellungEntity("Linus",
                                                      List.of(new Position("Monitor", 2, new BigDecimal("199.00")),
                                                              new Position("Kabel", 3, new BigDecimal("4.50"))),
                                                      Status.OFFEN);
        // Die ID vergibt sonst die Datenbank
        ReflectionTestUtils.setField(linus, "id", 2L);
        return linus;
    }

    @Test
    void mengeSetzen() {
        BestellungEntity linus = linus();
        linus.mengeSetzen(2, 5);
        assertThat(linus.alsBestellung().positionen()).containsExactly(new Position("Monitor",
                                                                                    2,
                                                                                    new BigDecimal("199.00")),
                                                                       new Position("Kabel",
                                                                                    5,
                                                                                    new BigDecimal("4.50")));
    }

    @Test
    void mengeSetzen2() {
        BestellungEntity linus = linus();

        assertThatThrownBy(() -> linus.mengeSetzen(3, 1)).isInstanceOf(NoSuchElementException.class)
                .hasMessage("Bestellung 2 hat keine Position 3");
    }
}