package de.bestellverwaltung.backend.bestellung;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(BestellController.class)
class BestellControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private BestellService service;

    private static final Bestellung ADA = new Bestellung(1,
                                                         "Ada",
                                                         List.of(new Position("Tastatur", 1, new BigDecimal("49.90"))),
                                                         Status.OFFEN,
                                                         Instant.parse("2026-10-10T18:26:18Z"));

    @Test
    void bestellungAlsJson() {
        when(service.finde(1)).thenReturn(Optional.of(ADA));

        assertThat(mvc.get().uri("/api/bestellungen/1")).hasStatusOk().bodyJson().isEqualTo("""
                                                                                                    {"id": 1, "kunde": "Ada", "status": "offen", "angelegtAm": "2026-10-10T18:26:18Z",
                                                                                                     "positionen": [{"artikel": "Tastatur", "menge": 1, "einzelpreis": 49.90}]}
                                                                                                    """);
    }

    @Test
    void unbekannteBestellungErgibt404() {
        when(service.finde(99)).thenReturn(Optional.empty());

        assertThat(mvc.get().uri("/api/bestellungen/99")).hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .isLenientlyEqualTo("""
                                            {"detail": "Bestellung 99 nicht gefunden"}
                                            """);
    }

    @Test
    void neueBestellungErgibt201MitLocation() {
        when(service.anlegen(any())).thenReturn(ADA);

        assertThat(mvc.post().uri("/api/bestellungen").contentType(MediaType.APPLICATION_JSON).content("""
                                                                                                               {"kunde": "Ada", "positionen": [{"artikel": "Tastatur", "menge": 1, "einzelpreis": 49.90}]}
                                                                                                               """)).hasStatus(
                HttpStatus.CREATED).hasHeader("Location", "/api/bestellungen/1");
    }

    @Test
    void ungueltigeBestellungErgibt400MitFeldfehlern() {
        assertThat(mvc.post().uri("/api/bestellungen").contentType(MediaType.APPLICATION_JSON).content("""
                                                                                                               {"kunde": " ", "positionen": []}
                                                                                                               """)).hasStatus(
                HttpStatus.BAD_REQUEST).bodyJson().isLenientlyEqualTo("""
                                                                              {"fehler": [{"feld": "kunde", "meldung": "Bitte einen Kunden angeben"},
                                                                                          {"feld": "positionen", "meldung": "Mindestens eine Position"}]}
                                                                              """);
    }

    @Test
    void stornierteBestellungErgibt409() {
        when(service.statusSetzen(3,
                                  Status.BEZAHLT)).thenThrow(new IllegalStateException("Bestellung 3 ist storniert"));

        assertThat(mvc.put().uri("/api/bestellungen/3/status").contentType(MediaType.APPLICATION_JSON).content("""
                                                                                                                       {"status": "bezahlt"}
                                                                                                                       """)).hasStatus(
                HttpStatus.CONFLICT).bodyJson().isLenientlyEqualTo("""
                                                                           {"detail": "Bestellung 3 ist storniert"}
                                                                           """);
    }
}