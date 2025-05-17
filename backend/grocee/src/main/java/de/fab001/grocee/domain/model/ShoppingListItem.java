package de.fab001.grocee.domain.model;

import jakarta.persistence.*;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class ShoppingListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    private ProductTemplate productTemplate;

    @ManyToOne(optional = false)
    @JsonBackReference
    private ShoppingList shoppingList;

    @Embedded
    private ExpirationDate expirationDate;
    private double price;
    private String neededBy;
    private String boughtBy;
    private boolean isPaid;

    // Default constructor for JPA
    public ShoppingListItem() {
    }

    public ShoppingListItem(ProductTemplate productTemplate, ShoppingList shoppingList, ExpirationDate expirationDate, double price, String neededBy, String boughtBy, boolean isPaid) {
        this.productTemplate = productTemplate;
        this.shoppingList = shoppingList;
        this.expirationDate = expirationDate;
        this.price = price;
        this.neededBy = neededBy;
        this.boughtBy = boughtBy;
        this.isPaid = isPaid;
    }
    
    public Long getId() { return id; }
    public ProductTemplate getProductTemplate() { return productTemplate; }
    public void setProductTemplate(ProductTemplate productTemplate) { this.productTemplate = productTemplate; }
    public ShoppingList getShoppingList() { return shoppingList; }
    public void setShoppingList(ShoppingList shoppingList) { this.shoppingList = shoppingList; }
    public ExpirationDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(ExpirationDate expirationDate) { this.expirationDate = expirationDate; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getNeededBy() { return neededBy; }
    public void setNeededBy(String neededBy) { this.neededBy = neededBy; }
    public String getBoughtBy() { return boughtBy; }
    public void setBoughtBy(String boughtBy) { this.boughtBy = boughtBy; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
    
    /**
     * Creates a Product from this ShoppingListItem.
     * This is a convenience method primarily for test compatibility.
     */
    public Product getProduct() {
        if (productTemplate == null) {
            return null;
        }
        
        Product product = new Product(
            productTemplate.getName(),
            productTemplate.getCategory(),
            productTemplate.getBrand(),
            this.expirationDate
        );
        
        // Use the ShoppingListItem ID as the product ID
        String productId = this.id != null ? this.id.toString() : UUID.randomUUID().toString();
        product.setId(productId);
        
        product.setPrice(this.price);
        product.setNeededBy(this.neededBy);
        product.setBoughtBy(this.boughtBy);
        product.setPaid(this.isPaid);
        product.setShoppingList(this.shoppingList);
        
        return product;
    }
} 