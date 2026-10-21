package de.bestellverwaltung.backend.bestellung;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty("offen") OFFEN,
    @JsonProperty("bezahlt") BEZAHLT,
    @JsonProperty("versandt") VERSANDT,
    @JsonProperty("storniert") STORNIERT
}
