package de.bestellverwaltung.backend.bestellung;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/bestellungen")
public class BestellController {

    BestellService service;

    public BestellController(BestellService service) {
        this.service = service;
    }

    private static ResponseStatusException nichtGefunden(long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Bestellung " + id + " nicht gefunden");
    }

    @GetMapping
    public List<Bestellung> alle() {
        return service.alle();
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Bestellung finde(@PathVariable long id) {
        return service.finde(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Bestellung> anlegen(@Valid @RequestBody NeueBestellung bestellung) {
        Bestellung neu = service.anlegen(bestellung);
        return ResponseEntity.created(URI.create("/api/bestellungen/" + neu.id())).body(neu);
    }

    @PutMapping("/{id}/status")
    public Bestellung statusSetzen(@PathVariable long id, @Valid @RequestBody StatusAenderung aenderung) {
        return service.statusSetzen(id, aenderung.status()).orElseThrow(() -> nichtGefunden(id));
    }
    
    @PutMapping("/{id}/positionen/{nr}")
    public Bestellung mengeSetzen(@PathVariable long id,
                                  @PathVariable int nr,
                                  @Valid @RequestBody MengenAenderung aenderung) {
        return service.mengeSetzen(id, nr, aenderung.menge()).orElseThrow(() -> nichtGefunden(id));
    }

    @ExceptionHandler
    public ProblemDetail fehlt(NoSuchElementException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    public ProblemDetail konflikt(IllegalStateException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler
    public ProblemDetail ungueltig(MethodArgumentNotValidException e) {
        ProblemDetail problem = e.getBody();
        problem.setProperty("fehler",
                            e.getFieldErrors()
                                    .stream()
                                    .sorted(Comparator.comparing(FieldError::getField))
                                    .map(f -> new Feldfehler(f.getField(), f.getDefaultMessage()))
                                    .toList());
        return problem;
    }

    record Feldfehler(String feld, String meldung) {
    }
}
