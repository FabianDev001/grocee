package de.fab001.grocee;

import de.fab001.grocee.domain.model.ExpirationDate;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.service.ExpirationChecker;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class ExpirationCheckerTest {

    @Test
    void findsOnlyExpiredProducts() {
        Product p1 = new Product("Milch", "Getränk", "Marke A",
                new ExpirationDate(LocalDate.now().minusDays(1)));
        Product p2 = new Product("Brot", "Backwaren", "Marke B",
                new ExpirationDate(LocalDate.now().plusDays(2)));
        Product p3 = new Product("Käse", "Kühlung", "Marke C",
                new ExpirationDate(LocalDate.now().minusDays(5)));

        List<Product> products = List.of(p1, p2, p3);
        ExpirationChecker prefer = new ExpirationChecker();

        List<Product> expired = prefer.findExpired(products);

        assertEquals(2, expired.size());
        assertTrue(expired.contains(p1));
        assertTrue(expired.contains(p3));
        assertFalse(expired.contains(p2));
    }

    @Test
    void findsProductsExpiringSoon() {
        Product p1 = new Product("Joghurt", "MilchProducte", "Marke X",
                new ExpirationDate(LocalDate.now().plusDays(1)));
        Product p2 = new Product("Reis", "Trockenware", "Marke Y",
                new ExpirationDate(LocalDate.now().plusDays(10)));

        List<Product> products = List.of(p1, p2);
        ExpirationChecker prefer = new ExpirationChecker();

        List<Product> closeToExpire = prefer.findCloseToExpire(products);

        assertEquals(1, closeToExpire.size());
        assertTrue(closeToExpire.contains(p1));
        assertFalse(closeToExpire.contains(p2));
    }
}
