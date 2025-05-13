package de.fab001.grocee;

import de.fab001.grocee.domain.model.ExpirationDate;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.ShoppingListItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TestShoppingListTest {

    private TestShoppingList testList;
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        testList = new TestShoppingList(id, "Test Einkaufsliste");
        
        testProduct = new Product(
            "Milch", 
            "Milchprodukte", 
            "Testmarke", 
            new ExpirationDate(LocalDate.now().plusDays(5))
        );
        testProduct.setPrice(2.99);
        testProduct.setNeededBy("Benutzer1");
        testProduct.setBoughtBy("Benutzer2");
    }
    
    @Test
    void addProduct_shouldAddToProductsList() {
        // Act
        testList.addProduct(testProduct);
        
        // Assert
        assertEquals(1, testList.getProduct().size());
        assertTrue(testList.getProduct().contains(testProduct));
    }
    
    @Test
    void addProduct_shouldSetShoppingListReference() {
        // Act
        testList.addProduct(testProduct);
        
        // Assert
        assertEquals(testList, testProduct.getShoppingList());
    }
    
    @Test
    void addProduct_shouldCreateShoppingListItem() {
        // Act
        testList.addProduct(testProduct);
        
        // Assert
        assertEquals(1, testList.getItems().size());
        
        ShoppingListItem item = testList.getItems().get(0);
        assertNotNull(item);
        assertEquals(testProduct.getName(), item.getProductTemplate().getName());
        assertEquals(testProduct.getCategory(), item.getProductTemplate().getCategory());
        assertEquals(testProduct.getBrand(), item.getProductTemplate().getBrand());
        assertEquals(testProduct.getExpirationDate(), item.getExpirationDate());
        assertEquals(testProduct.getPrice(), item.getPrice());
        assertEquals(testProduct.getNeededBy(), item.getNeededBy());
        assertEquals(testProduct.getBoughtBy(), item.getBoughtBy());
        assertEquals(testProduct.isPaid(), item.isPaid());
    }
    
    @Test
    void getProduct_shouldReturnAddedProducts() {
        // Arrange
        Product product1 = new Product("Brot", "Backwaren", "Bäckerei", 
                new ExpirationDate(LocalDate.now().plusDays(2)));
        Product product2 = new Product("Butter", "Milchprodukte", "Molkerei", 
                new ExpirationDate(LocalDate.now().plusDays(10)));
        
        // Act
        testList.addProduct(product1);
        testList.addProduct(product2);
        
        // Assert
        assertEquals(2, testList.getProduct().size());
        assertTrue(testList.getProduct().contains(product1));
        assertTrue(testList.getProduct().contains(product2));
    }
    
    @Test
    void constructor_withIdAndName_shouldSetValues() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Neue Liste";
        
        // Act
        TestShoppingList list = new TestShoppingList(id, name);
        
        // Assert
        assertEquals(id, list.getId());
        assertEquals(name, list.getName());
    }
} 