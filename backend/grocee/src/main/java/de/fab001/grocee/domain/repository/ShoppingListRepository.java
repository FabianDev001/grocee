package de.fab001.grocee.domain.repository;

import de.fab001.grocee.domain.model.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, String> {
    // Zusätzliche Query-Methoden können hier ergänzt werden
}
