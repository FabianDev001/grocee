package de.fab001.grocee.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public final class Haltbarkeitsdatum {
    private final LocalDate datum;

    public Haltbarkeitsdatum(LocalDate datum) {
        if (datum == null || datum.isBefore(LocalDate.now().minusYears(5))) {
            throw new IllegalArgumentException("Ungültiges Haltbarkeitsdatum");
        }
        this.datum = datum;
    }

    public boolean istAbgelaufen() {
        return datum.isBefore(LocalDate.now());
    }

    public boolean istKurzVorAblauf() {
        return datum.isBefore(LocalDate.now().plusDays(3));
    }

    public LocalDate getDatum() {
        return datum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Haltbarkeitsdatum)) return false;
        Haltbarkeitsdatum that = (Haltbarkeitsdatum) o;
        return datum.equals(that.datum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datum);
    }
}
