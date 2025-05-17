package de.fab001.grocee.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Converter to handle SQLite date storage issues.
 * Stores dates as milliseconds since epoch for consistent handling.
 */
@Converter(autoApply = true)
public class SQLiteDateConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        
        // Convert to milliseconds since epoch and create a date from that
        long epochMilli = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new Date(epochMilli);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date) {
        if (date == null) {
            return null;
        }
        
        try {
            // For timestamp stored as number
            long epochMilli = date.getTime();
            return Instant.ofEpochMilli(epochMilli)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } catch (Exception e) {
            System.err.println("Failed to convert date: " + date + " - " + e.getMessage());
            return LocalDate.now();
        }
    }
} 