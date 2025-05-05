package de.fab001.grocee.domain.model;

import java.time.LocalDate;
import java.util.Objects;

public final class ExpirationDate {
    private final LocalDate date;

    public ExpirationDate(LocalDate datum) {
        if (datum == null || datum.isBefore(LocalDate.now().minusYears(5))) {
            throw new IllegalArgumentException("Ungültiges Haltbarkeitsdatum");
        }
        this.date = datum;
    }

    public boolean isExpired() {
        return date.isBefore(LocalDate.now());
    }

    public boolean isCloseToExpire() {
        return date.isBefore(LocalDate.now().plusDays(3));
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpirationDate)) return false;
        ExpirationDate that = (ExpirationDate) o;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
