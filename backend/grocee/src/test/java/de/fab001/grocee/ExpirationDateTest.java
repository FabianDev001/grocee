package de.fab001.grocee;

import de.fab001.grocee.domain.model.ExpirationDate;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ExpirationDateTest {

    @Test
    void defaultConstructor_setsDateToSevenDaysFromNow() {
        ExpirationDate expirationDate = new ExpirationDate();
        assertEquals(LocalDate.now().plusDays(7), expirationDate.getDate());
    }

    @Test
    void constructor_withValidDate_setsDate() {
        LocalDate date = LocalDate.now().plusDays(10);
        ExpirationDate expirationDate = new ExpirationDate(date);
        assertEquals(date, expirationDate.getDate());
    }

    @Test
    void constructor_withNullDate_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ExpirationDate(null);
        });
        assertTrue(exception.getMessage().contains("Ungültiges Haltbarkeitsdatum"));
    }

    @Test
    void constructor_withDateTooFarInPast_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ExpirationDate(LocalDate.now().minusYears(6));
        });
        assertTrue(exception.getMessage().contains("Ungültiges Haltbarkeitsdatum"));
    }

    @Test
    void isExpired_withPastDate_returnsTrue() {
        ExpirationDate expirationDate = new ExpirationDate(LocalDate.now().minusDays(1));
        assertTrue(expirationDate.isExpired());
    }

    @Test
    void isExpired_withFutureDate_returnsFalse() {
        ExpirationDate expirationDate = new ExpirationDate(LocalDate.now().plusDays(1));
        assertFalse(expirationDate.isExpired());
    }

    @Test
    void isExpired_withTodayDate_returnsFalse() {
        ExpirationDate expirationDate = new ExpirationDate(LocalDate.now());
        assertFalse(expirationDate.isExpired());
    }

    @Test
    void isCloseToExpire_withDateWithinSevenDays_returnsTrue() {
        ExpirationDate expirationDate = new ExpirationDate(LocalDate.now().plusDays(6));
        assertTrue(expirationDate.isCloseToExpire());
    }

    @Test
    void isCloseToExpire_withDateBeyondSevenDays_returnsFalse() {
        ExpirationDate expirationDate = new ExpirationDate(LocalDate.now().plusDays(7));
        assertFalse(expirationDate.isCloseToExpire());
    }

    @Test
    void isCloseToExpire_withExpiredDate_returnsFalse() {
        ExpirationDate expirationDate = new ExpirationDate(LocalDate.now().minusDays(1));
        assertFalse(expirationDate.isCloseToExpire());
    }

    @Test
    void equals_withSameDate_returnsTrue() {
        LocalDate date = LocalDate.now().plusDays(10);
        ExpirationDate date1 = new ExpirationDate(date);
        ExpirationDate date2 = new ExpirationDate(date);
        assertEquals(date1, date2);
    }

    @Test
    void equals_withDifferentDate_returnsFalse() {
        ExpirationDate date1 = new ExpirationDate(LocalDate.now().plusDays(10));
        ExpirationDate date2 = new ExpirationDate(LocalDate.now().plusDays(11));
        assertNotEquals(date1, date2);
    }

    @Test
    void hashCode_withSameDate_returnsSameValue() {
        LocalDate date = LocalDate.now().plusDays(10);
        ExpirationDate date1 = new ExpirationDate(date);
        ExpirationDate date2 = new ExpirationDate(date);
        assertEquals(date1.hashCode(), date2.hashCode());
    }
} 