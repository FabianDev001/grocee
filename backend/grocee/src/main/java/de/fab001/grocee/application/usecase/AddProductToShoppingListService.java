package de.fab001.grocee.application.usecase;

import de.fab001.grocee.domain.model.Einkaufsliste;
import de.fab001.grocee.domain.model.Produkt;
import de.fab001.grocee.domain.repository.EinkaufslisteRepository;

import java.util.Optional;
import java.util.UUID;

public class AddProductToShoppingListService {

    private final EinkaufslisteRepository repository;

    public AddProductToShoppingListService(EinkaufslisteRepository repository) {
        this.repository = repository;
    }

    public void produktHinzufügen(UUID einkaufslisteId, Produkt produkt) {
        Optional<Einkaufsliste> optionalListe = repository.findById(einkaufslisteId);
        if (optionalListe.isEmpty()) {
            throw new IllegalArgumentException("Einkaufsliste nicht gefunden");
        }

        Einkaufsliste liste = optionalListe.get();
        liste.produktHinzufügen(produkt);
        repository.safe(liste);
    }
}
