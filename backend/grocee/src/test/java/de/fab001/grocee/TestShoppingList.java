package de.fab001.grocee;

import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.ProductTemplate;
import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.domain.model.ShoppingListItem;
import de.fab001.grocee.domain.service.ProductTemplateService;

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
    
    @Transient
    private ProductTemplateService productTemplateService;
    
    public TestShoppingList() {
        super();
    }
    
    public TestShoppingList(UUID id, String name) {
        super(id, name);
    }
    
    /**
     * Injects the ProductTemplateService for proper template persistence.
     * This is used by test methods to ensure templates are correctly stored.
     */
    public void setProductTemplateService(ProductTemplateService service) {
        this.productTemplateService = service;
    }
    
    /**
     * Adds a product to this shopping list.
     * This method is added purely for testing convenience.
     * @throws IllegalArgumentException if a product with the same name already exists
     */
    @Override
    public void addProduct(Product product) {
        // Check if a product with the same name already exists
        if (containsProductWithName(product.getName())) {
            throw new IllegalArgumentException("Ein Produkt mit dem Namen '" + product.getName() + "' existiert bereits in dieser Einkaufsliste.");
        }
        
        products.add(product);
        product.setShoppingList(this);
        
        // Get a properly persisted template
        ProductTemplate template;
        if (productTemplateService != null) {
            template = productTemplateService.findOrCreate(
                product.getName(), 
                product.getBrand(), 
                product.getCategory()
            );
        } else {
            // Fallback for when service is not available
            template = new ProductTemplate(
                product.getName(),
                product.getBrand(),
                product.getCategory()
            );
        }
        
        // Create ShoppingListItem with the template
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