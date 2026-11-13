package de.bestellverwaltung.backend.bestellung;

import static org.assertj.core.api.Assertions.assertThat;

import de.bestellverwaltung.backend.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BestellungIntegrationIT {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void angelegteBestellungLaesstSichLaden() {
        MvcTestResult angelegt = mvc.post()
                .uri("/api/bestellungen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                 {"kunde": "Barbara", "positionen": [{"artikel": "Maus", "menge": 2, "einzelpreis": 25}]}
                                 """)
                .exchange();
        assertThat(angelegt).hasStatus(HttpStatus.CREATED);
        String adresse = angelegt.getResponse().getHeader("Location");

        assertThat(mvc.get().uri(adresse)).hasStatusOk().bodyJson().isLenientlyEqualTo("""
                                                                                               {"kunde": "Barbara", "status": "offen",
                                                                                                "positionen": [{"artikel": "Maus", "menge": 2, "einzelpreis": 25.00}]}
                                                                                               """);
    }

    @Test
    void geaenderterStatusIstGespeichert() {
        assertThat(mvc.put().uri("/api/bestellungen/1/status").contentType(MediaType.APPLICATION_JSON).content("""
                                                                                                                       {"status": "bezahlt"}
                                                                                                                       """)).hasStatusOk();

        assertThat(mvc.get().uri("/api/bestellungen/1")).bodyJson().isLenientlyEqualTo("""
                                                                                               {"kunde": "Ada", "status": "bezahlt"}
                                                                                               """);
    }
}
