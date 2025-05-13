package de.fab001.grocee;

import de.fab001.grocee.domain.model.ExpirationDate;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.ShoppingList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ShoppingListTest {

    private ShoppingList shoppingList;
    private Product product;

    @BeforeEach
    void setUp() {
        shoppingList = new ShoppingList(UUID.randomUUID(), "Test Liste");
        product = new Product("Milch", "Milchprodukte", "Weihenstephan", 
                new ExpirationDate(LocalDate.now().plusDays(5)));
    }

    @Test
    void containsProductWithName_withExistingProduct_returnsTrue() {
        // Arrange
        shoppingList.addProduct(product);
        
        // Act & Assert
        assertTrue(shoppingList.containsProductWithName("Milch"));
    }
    
    @Test
    void containsProductWithName_withNonExistingProduct_returnsFalse() {
        // Act & Assert
        assertFalse(shoppingList.containsProductWithName("Käse"));
    }
    
    @Test
    void containsProductWithName_withDifferentCase_returnsTrue() {
        // Arrange
        shoppingList.addProduct(product);
        
        // Act & Assert
        assertTrue(shoppingList.containsProductWithName("milch"));
        assertTrue(shoppingList.containsProductWithName("MILCH"));
    }
    
    @Test
    void containsProductWithName_withNull_returnsFalse() {
        // Act & Assert
        assertFalse(shoppingList.containsProductWithName(null));
    }
    
    @Test
    void addProduct_withDuplicateName_throwsException() {
        // Arrange
        shoppingList.addProduct(product);
        
        Product duplicateProduct = new Product("Milch", "Milchprodukte", "Anderer Hersteller", 
                new ExpirationDate(LocalDate.now().plusDays(10)));
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            shoppingList.addProduct(duplicateProduct);
        });
        
        assertTrue(exception.getMessage().contains("existiert bereits"));
    }
    
    @Test
    void addProduct_withDifferentCase_throwsException() {
        // Arrange
        shoppingList.addProduct(product);
        
        Product duplicateProduct = new Product("MILCH", "Milchprodukte", "Anderer Hersteller", 
                new ExpirationDate(LocalDate.now().plusDays(10)));
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            shoppingList.addProduct(duplicateProduct);
        });
        
        assertTrue(exception.getMessage().contains("existiert bereits"));
    }
    
    @Test
    void addProduct_withDifferentProducts_succeeds() {
        // Arrange
        shoppingList.addProduct(product);
        
        Product differentProduct = new Product("Käse", "Milchprodukte", "Hersteller", 
                new ExpirationDate(LocalDate.now().plusDays(10)));
        
        // Act & Assert
        assertDoesNotThrow(() -> {
            shoppingList.addProduct(differentProduct);
        });
        
        assertEquals(2, shoppingList.getItems().size());
    }
} 