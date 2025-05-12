package de.fab001.grocee.domain.model;

import java.time.LocalDate;
import java.util.Objects;
import jakarta.persistence.*;

@Embeddable
public final class ExpirationDate {
    @Column(name = "expiration_date")
    private LocalDate date;

    // Konstruktur für die Tests notwendig
    public ExpirationDate() {
        this.date = LocalDate.now().plusDays(7);
    }

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
        return date.isBefore(LocalDate.now().plusDays(7)) && !isExpired();
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
