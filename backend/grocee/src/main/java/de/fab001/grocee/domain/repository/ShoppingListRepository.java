package de.fab001.grocee.domain.repository;

import de.fab001.grocee.domain.model.ShoppingList;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingListRepository {
    Optional<ShoppingList> findById(UUID id);
    void safe(ShoppingList liste);
}
