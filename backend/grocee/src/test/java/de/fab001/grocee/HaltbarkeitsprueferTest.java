package de.fab001.grocee;

import de.fab001.grocee.domain.model.Haltbarkeitsdatum;
import de.fab001.grocee.domain.model.Produkt;
import de.fab001.grocee.domain.service.Haltbarkeitspruefer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class HaltbarkeitsprueferTest {

    @Test
    void findsOnlyExpiredProducts() {
        Produkt p1 = new Produkt("Milch", "Getränk", "Marke A",
                new Haltbarkeitsdatum(LocalDate.now().minusDays(1)));
        Produkt p2 = new Produkt("Brot", "Backwaren", "Marke B",
                new Haltbarkeitsdatum(LocalDate.now().plusDays(2)));
        Produkt p3 = new Produkt("Käse", "Kühlung", "Marke C",
                new Haltbarkeitsdatum(LocalDate.now().minusDays(5)));

        List<Produkt> produkte = List.of(p1, p2, p3);
        Haltbarkeitspruefer prefer = new Haltbarkeitspruefer();

        List<Produkt> abgelaufen = prefer.findeAbgelaufene(produkte);

        assertEquals(2, abgelaufen.size());
        assertTrue(abgelaufen.contains(p1));
        assertTrue(abgelaufen.contains(p3));
        assertFalse(abgelaufen.contains(p2));
    }

    @Test
    void findsProductsExpiringSoon() {
        Produkt p1 = new Produkt("Joghurt", "Milchprodukte", "Marke X",
                new Haltbarkeitsdatum(LocalDate.now().plusDays(1)));
        Produkt p2 = new Produkt("Reis", "Trockenware", "Marke Y",
                new Haltbarkeitsdatum(LocalDate.now().plusDays(10)));

        List<Produkt> produkte = List.of(p1, p2);
        Haltbarkeitspruefer prefer = new Haltbarkeitspruefer();

        List<Produkt> baldAbgelaufen = prefer.findeKurzVorAblauf(produkte);

        assertEquals(1, baldAbgelaufen.size());
        assertTrue(baldAbgelaufen.contains(p1));
        assertFalse(baldAbgelaufen.contains(p2));
    }
}
