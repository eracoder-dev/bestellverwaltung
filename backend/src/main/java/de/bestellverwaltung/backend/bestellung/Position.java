package de.bestellverwaltung.backend.bestellung;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record Position(@NotBlank(message = "Artikel fehlt") String artikel,
                       @Min(value = 1, message = "Menge mindestens 1") int menge,
                       @NotNull @Positive(message = "Preis muss größer als 0 sein") BigDecimal einzelpreis) {
}
