package de.fab001.grocee.domain.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public final class ExpirationDate {
    @Column(name = "expiration_date")
    private String dateStr;

    // Default constructor for JPA
    public ExpirationDate() {
        this.dateStr = LocalDate.now().plusDays(7).toString();
    }

    // Constructor for direct LocalDate
    public ExpirationDate(LocalDate datum) {
        if (datum == null || datum.isBefore(LocalDate.now().minusYears(5))) {
            throw new IllegalArgumentException("Ungültiges Haltbarkeitsdatum");
        }
        this.dateStr = datum.toString();
    }
    
    // Constructor for JSON deserialization - only used by Jackson
    @JsonCreator
    public static ExpirationDate fromJson(@JsonProperty("date") String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return new ExpirationDate();
        }
        
        // Try parsing with ISO_LOCAL_DATE format (YYYY-MM-DD)
        try {
            return new ExpirationDate(LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE));
        } catch (DateTimeParseException e) {
            // Try parsing with ISO_DATE_TIME format as fallback
            try {
                LocalDate parsedDate = LocalDate.parse(dateString.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
                return new ExpirationDate(parsedDate);
            } catch (Exception ex) {
                // Default to a week from now if parsing fails
                System.err.println("Failed to parse date: " + dateString + ", using default");
                return new ExpirationDate();
            }
        }
    }

    /**
     * Parse a date string in ISO format (YYYY-MM-DD)
     * @param dateString The date string
     * @return A new ExpirationDate
     */
    public static ExpirationDate fromString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return new ExpirationDate();
        }
        try {
            return new ExpirationDate(LocalDate.parse(dateString));
        } catch (Exception e) {
            // Fallback if date is in another format or invalid
            return new ExpirationDate();
        }
    }

    public boolean isExpired() {
        return getDate().isBefore(LocalDate.now());
    }

    public boolean isCloseToExpire() {
        return getDate().isBefore(LocalDate.now().plusDays(7)) && !isExpired();
    }

    public LocalDate getDate() {
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.err.println("Failed to parse stored date: " + dateStr);
            return LocalDate.now();
        }
    }

    @Override
    public String toString() {
        return dateStr != null ? dateStr : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpirationDate)) return false;
        ExpirationDate that = (ExpirationDate) o;
        return Objects.equals(dateStr, that.dateStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateStr);
    }
}
