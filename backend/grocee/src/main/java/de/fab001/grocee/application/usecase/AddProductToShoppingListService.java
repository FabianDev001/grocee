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

    public void addProduct(UUID shoppingListId, Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Produktname (name) ist erforderlich.");
        }
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("Kategorie (category) ist erforderlich.");
        }
        if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Marke (brand) ist erforderlich.");
        }
        if (product.getExpirationDate() == null) {
            throw new IllegalArgumentException("Haltbarkeitsdatum (expiration) ist erforderlich.");
        }
        Optional<ShoppingList> optionalList = repository.findById(shoppingListId.toString());
        if (optionalList.isEmpty()) {
            throw new IllegalArgumentException("Einkaufsliste nicht gefunden");
        }

        ShoppingList list = optionalList.get();
        list.addProduct(product);
        repository.save(list);
    }

    public void setBuyer(UUID shoppingListId, String productId, String buyerId) {
        Optional<ShoppingList> optionalList = repository.findById(shoppingListId.toString());
        if (optionalList.isEmpty()) {
            throw new IllegalArgumentException("Einkaufsliste nicht gefunden");
        }
        if (buyerId == null) {
            throw new IllegalArgumentException("Käufer nicht gefunden");
        }

        ShoppingList list = optionalList.get();
        list.findById(UUID.fromString(productId)).setBoughtBy(buyerId);
        repository.save(list);
    }

    public void setBuyer(UUID shoppingListId, String productId) {
        Optional<ShoppingList> optionalList = repository.findById(shoppingListId.toString());
        ShoppingList list = optionalList.get();

        list.findById(UUID.fromString(productId)).setBoughtBy(null);
        repository.save(list);
    }

}
