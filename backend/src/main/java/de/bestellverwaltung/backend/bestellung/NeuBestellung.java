package de.bestellverwaltung.backend.bestellung;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record NeuBestellung(@NotBlank(message = "Bitte einen Kunden angeben") String kunde,
                            @NotEmpty(message = "Mindestens eine Position") List<Position> positionen) {

}
