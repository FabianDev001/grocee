package de.fab001.grocee;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fab001.grocee.domain.model.ExpirationDate;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.domain.repository.ShoppingListRepository;
import de.fab001.grocee.domain.service.ProductTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DuplicateProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShoppingListRepository shoppingListRepository;
    
    @Autowired
    private ProductTemplateService productTemplateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addProduct_withDuplicateName_returnsBadRequest() throws Exception {
        // Create a shopping list with a product
        UUID listId = UUID.randomUUID();
        TestShoppingList list = new TestShoppingList(listId, "Test List");
        list.setProductTemplateService(productTemplateService);
        
        Product existingProduct = new Product("Milch", "Milchprodukte", "Weihenstephan", 
                new ExpirationDate(LocalDate.now().plusDays(5)));
        existingProduct.setNeededBy(UUID.randomUUID().toString());
        
        list.addProduct(existingProduct);
        shoppingListRepository.save(list);
        
        // Try to add a product with the same name
        Product duplicateProduct = new Product("Milch", "Milchprodukte", "Andere Marke", 
                new ExpirationDate(LocalDate.now().plusDays(10)));
        duplicateProduct.setNeededBy(UUID.randomUUID().toString());
        
        mockMvc.perform(post("/shoppinglists/" + listId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("existiert bereits")));
    }
    
    @Test
    void addProduct_withDifferentCase_returnsBadRequest() throws Exception {
        // Create a shopping list with a product
        UUID listId = UUID.randomUUID();
        TestShoppingList list = new TestShoppingList(listId, "Test List");
        list.setProductTemplateService(productTemplateService);
        
        Product existingProduct = new Product("Milch", "Milchprodukte", "Weihenstephan", 
                new ExpirationDate(LocalDate.now().plusDays(5)));
        existingProduct.setNeededBy(UUID.randomUUID().toString());
        
        list.addProduct(existingProduct);
        shoppingListRepository.save(list);
        
        // Try to add a product with the same name but different case
        Product duplicateProduct = new Product("MILCH", "Milchprodukte", "Andere Marke", 
                new ExpirationDate(LocalDate.now().plusDays(10)));
        duplicateProduct.setNeededBy(UUID.randomUUID().toString());
        
        mockMvc.perform(post("/shoppinglists/" + listId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("existiert bereits")));
    }
    
    @Test
    void addProduct_withUniqueName_returnsCreated() throws Exception {
        // Create a shopping list with a product
        UUID listId = UUID.randomUUID();
        TestShoppingList list = new TestShoppingList(listId, "Test List");
        list.setProductTemplateService(productTemplateService);
        
        Product existingProduct = new Product("Milch", "Milchprodukte", "Weihenstephan", 
                new ExpirationDate(LocalDate.now().plusDays(5)));
        existingProduct.setNeededBy(UUID.randomUUID().toString());
        
        list.addProduct(existingProduct);
        shoppingListRepository.save(list);
        
        // Add a product with a different name
        Product uniqueProduct = new Product("Käse", "Milchprodukte", "Hersteller", 
                new ExpirationDate(LocalDate.now().plusDays(10)));
        String neededBy = UUID.randomUUID().toString();
        uniqueProduct.setNeededBy(neededBy);
        
        // Print the exact JSON being sent for debugging
        System.out.println("Sending JSON: " + objectMapper.writeValueAsString(uniqueProduct));
        System.out.println("neededBy value: " + neededBy);
        
        // Perform the assertion
        mockMvc.perform(post("/shoppinglists/" + listId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(uniqueProduct)))
                .andExpect(status().isCreated());
    }
} 