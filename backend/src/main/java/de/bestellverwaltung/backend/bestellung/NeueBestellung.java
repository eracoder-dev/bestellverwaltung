package de.bestellverwaltung.backend.bestellung;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record NeueBestellung(
        @Size(max = 30, message = "Höchstens 30 Zeichen") @NotBlank(message = "Bitte einen Kunden angeben") String kunde,
        @NotEmpty(message = "Mindestens eine Position") List<Position> positionen) {

}
