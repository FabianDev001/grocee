package de.fab001.grocee;

import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.ProductTemplate;
import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.domain.model.ShoppingListItem;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Extension of ShoppingList for testing purposes.
 * Adds temporary methods needed for tests until the real implementation matches the test requirements.
 */
@Entity
@DiscriminatorValue("Test")
public class TestShoppingList extends ShoppingList {
    
    @Transient
    private final List<Product> products = new ArrayList<>();
    
    public TestShoppingList() {
        super();
    }
    
    public TestShoppingList(UUID id, String name) {
        super(id, name);
    }
    
    /**
     * Adds a product to this shopping list.
     * This method is added purely for testing convenience.
     */
    public void addProduct(Product product) {
        products.add(product);
        product.setShoppingList(this);
        
        // Create a proper ShoppingListItem for persistence
        ProductTemplate template = new ProductTemplate(
            product.getName(),
            product.getBrand(),
            product.getCategory()
        );
        
        ShoppingListItem item = new ShoppingListItem(
            template,
            this,
            product.getExpirationDate(),
            product.getPrice(),
            product.getNeededBy(),
            product.getBoughtBy(),
            product.isPaid()
        );
        
        // Add to the real list
        addItem(item);
    }
    
    /**
     * Gets the list of products.
     * This method is added purely for testing convenience.
     */
    public List<Product> getProduct() {
        return products;
    }
} 