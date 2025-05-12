package de.fab001.grocee.domain.service;

import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.domain.repository.ShoppingListRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShoppingListService {
    private final ShoppingListRepository repository;

    public ShoppingListService(ShoppingListRepository repository) {
        this.repository = repository;
    }

    public ShoppingList createShoppingList(ShoppingList list) {
        if (list.getName() == null || list.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name der Einkaufsliste darf nicht leer sein");
        }
        repository.save(list);
        return list;
    }

    public Optional<ShoppingList> findById(UUID id) {
        return repository.findById(id.toString());
    }

    public List<ShoppingList> findAll() {
        return repository.findAll();
    }

    public ShoppingList updateShoppingList(UUID id, ShoppingList updated) {
        ShoppingList existing = repository.findById(id.toString()).orElseThrow(() -> new IllegalArgumentException("Einkaufsliste nicht gefunden"));
        if (updated.getName() == null || updated.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name der Einkaufsliste darf nicht leer sein");
        }
        existing.setName(updated.getName());
        repository.save(existing);
        return existing;
    }

    public void deleteById(UUID id) {
        repository.deleteById(id.toString());
    }
} 