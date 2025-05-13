package de.fab001.grocee.domain.model;

import java.util.*;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public class ShoppingList {
    @Id
    @Column(length = 36)
    private String id;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ShoppingListItem> items = new ArrayList<>();

    private String name;

    public ShoppingList() {
        this.id = UUID.randomUUID().toString();
        this.name = null;
    }

    public ShoppingList(UUID id, String name) {
        this.id = id.toString();
        this.name = name;
    }

    public void addItem(ShoppingListItem item) {
        items.add(item);
        item.setShoppingList(this);
    }

    public void addProduct(Product product) {
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
        
        addItem(item);
    }
    
    /**
     * Removes a product from this shopping list by its UUID.
     * @param productId the UUID of the product to remove
     * @return true if the product was found and removed, false otherwise
     */
    public boolean removeProduct(UUID productId) {
        Optional<ShoppingListItem> itemToRemove = items.stream()
            .filter(item -> {
                Product product = item.getProduct();
                return product != null && product.getId().equals(productId);
            })
            .findFirst();
            
        if (itemToRemove.isPresent()) {
            items.remove(itemToRemove.get());
            return true;
        }
        return false;
    }
    
    public List<Product> getProduct() {
        return items.stream()
            .map(ShoppingListItem::getProduct)
            .toList();
    }

    public List<ShoppingListItem> getItems() {
        return items;
    }

    public Product findById(UUID productId) {
        return items.stream()
            .filter(p -> p.getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Produkt nicht gefunden"))
            .getProduct();
    }

    public UUID getId() {
        return UUID.fromString(id);
    }

    public void setId(UUID id) {
        this.id = id.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
