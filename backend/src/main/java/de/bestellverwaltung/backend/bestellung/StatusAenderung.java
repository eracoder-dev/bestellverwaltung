package de.bestellverwaltung.backend.bestellung;

import jakarta.validation.constraints.NotNull;

public record StatusAenderung(@NotNull Status status) {
}
