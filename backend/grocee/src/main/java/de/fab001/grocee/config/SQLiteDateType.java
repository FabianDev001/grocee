package de.fab001.grocee.config;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Custom Hibernate type to store LocalDate as long (milliseconds since epoch) in SQLite
 */
public class SQLiteDateType implements UserType<LocalDate> {

    @Override
    public int getSqlType() {
        return Types.VARCHAR; // SQLite stores everything as text
    }

    @Override
    public Class<LocalDate> returnedClass() {
        return LocalDate.class;
    }

    @Override
    public boolean equals(LocalDate x, LocalDate y) {
        return x == y || (x != null && y != null && x.equals(y));
    }

    @Override
    public int hashCode(LocalDate x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public LocalDate nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        if (rs.wasNull() || value == null) {
            return null;
        }
        
        try {
            // Parse the string as milliseconds since epoch
            Long epochMilli = Long.parseLong(value);
            return Instant.ofEpochMilli(epochMilli)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } catch (NumberFormatException e) {
            try {
                // Fallback for ISO date format
                return LocalDate.parse(value);
            } catch (Exception ex) {
                System.err.println("Failed to parse date value: " + value);
                return LocalDate.now();
            }
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, LocalDate value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            // Store as milliseconds since epoch
            long epochMilli = value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            st.setString(index, String.valueOf(epochMilli));
        }
    }

    @Override
    public LocalDate deepCopy(LocalDate value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(LocalDate value) {
        return value == null ? null : value.toString();
    }

    @Override
    public LocalDate assemble(Serializable cached, Object owner) {
        return (cached == null) ? null : LocalDate.parse((String) cached);
    }

    @Override
    public LocalDate replace(LocalDate detached, LocalDate managed, Object owner) {
        return detached;
    }
} 