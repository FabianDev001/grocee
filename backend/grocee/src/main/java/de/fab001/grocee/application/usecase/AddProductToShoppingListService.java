package de.fab001.grocee.application.usecase;

import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.repository.ShoppingListRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AddProductToShoppingListService {

    private final ShoppingListRepository repository;

    public AddProductToShoppingListService(ShoppingListRepository repository) {
        this.repository = repository;
    }

    public void addProduct(UUID einkaufslisteId, Product product) {
        Optional<ShoppingList> optionalList = repository.findById(einkaufslisteId);
        if (optionalList.isEmpty()) {
            throw new IllegalArgumentException("Einkaufsliste nicht gefunden");
        }

        ShoppingList list = optionalList.get();
        list.addProduct(product);
        repository.safe(list);
    }
}
