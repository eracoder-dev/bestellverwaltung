package de.bestellverwaltung.backend.bestellung;

import jakarta.validation.constraints.Min;

public record MengenAenderung(@Min(value = 1, message = "Menge mindestens 1") int menge) {
}
