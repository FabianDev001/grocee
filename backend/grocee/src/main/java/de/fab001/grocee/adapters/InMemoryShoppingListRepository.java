package de.fab001.grocee.adapters;

import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.domain.repository.ShoppingListRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryShoppingListRepository implements ShoppingListRepository {
    private final Map<UUID, ShoppingList> store = new HashMap<>();

    @Override
    public Optional<ShoppingList> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void safe(ShoppingList liste) {
        store.put(liste.getId(), liste);
    }
} 