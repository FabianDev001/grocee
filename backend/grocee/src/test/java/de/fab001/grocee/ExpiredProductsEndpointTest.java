package de.fab001.grocee;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fab001.grocee.domain.model.ExpirationDate;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.ProductTemplate;
import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.domain.model.ShoppingListItem;
import de.fab001.grocee.domain.repository.ShoppingListRepository;
import de.fab001.grocee.domain.service.ProductTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ExpiredProductsEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShoppingListRepository shoppingListRepository;
    
    @Autowired
    private ProductTemplateService productTemplateService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Helper method to create a ShoppingList with product items and ensure it's properly saved
     */
    private ShoppingList createTestShoppingList(UUID id, String name) {
        // Use regular ShoppingList instead of TestShoppingList
        ShoppingList list = new ShoppingList(id, name);
        shoppingListRepository.save(list); // Save before adding items to ensure it exists
        return list;
    }
    
    /**
     * Helper method to add a product to a shopping list
     */
    private void addProductToList(ShoppingList list, String name, String category, String brand, 
                                 LocalDate expirationDate, String neededBy, String boughtBy, boolean isPaid) {
        // Create a ProductTemplate using the service to avoid uniqueness constraint violations
        ProductTemplate template = productTemplateService.findOrCreate(name, brand, category);
        
        // Create a ShoppingListItem for the product
        ShoppingListItem item = new ShoppingListItem(
            template,
            list,
            expirationDate != null ? new ExpirationDate(expirationDate) : null,
            0.0, // price
            neededBy != null ? neededBy : UUID.randomUUID().toString(),
            boughtBy,
            isPaid
        );
        
        // Add the item to the list
        list.addItem(item);
        
        // Save the updated list
        shoppingListRepository.save(list);
    }

    @Test
    void getExpiringProducts_allProductStatuses_returnsCorrectClassification() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = createTestShoppingList(listId, "Test Shopping List");

        // Add products with various expiration dates
        addProductToList(list, "Eggs", "Dairy", "Farm Fresh", 
                LocalDate.now().minusDays(2), null, null, false);
        
        addProductToList(list, "Bread", "Bakery", "Local Bakery",
                LocalDate.now(), null, null, false);
        
        addProductToList(list, "Milk", "Dairy", "Brand A",
                LocalDate.now().plusDays(3), null, null, false);
        
        addProductToList(list, "Yogurt", "Dairy", "Brand B",
                LocalDate.now().plusDays(6), null, null, false);
        
        addProductToList(list, "Cheese", "Dairy", "Brand C",
                LocalDate.now().plusDays(7), null, null, false);
        
        addProductToList(list, "Canned Beans", "Canned Goods", "Brand D",
                LocalDate.now().plusDays(365), null, null, false);

        // Perform the request and validate response
        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Eggs')].status").value("expired"))
                .andExpect(jsonPath("$[?(@.name=='Bread')].status").value("expiring"))
                .andExpect(jsonPath("$[?(@.name=='Milk')].status").value("expiring"))
                .andExpect(jsonPath("$[?(@.name=='Yogurt')].status").value("expiring"))
                .andExpect(jsonPath("$[?(@.name=='Cheese')].status").value("ok"))
                .andExpect(jsonPath("$[?(@.name=='Canned Beans')].status").value("ok"));
    }

    @Test
    void getExpiringProducts_productWithNullExpirationDate_returnsUnknownStatus() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = createTestShoppingList(listId, "Test List");
        
        // Add a product with null expiration date
        addProductToList(list, "Test Product", "Test Category", "Test Brand", 
                        null, UUID.randomUUID().toString(), null, false);

        // Perform request and validate response
        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Test Product')].status").value("unknown"));
    }

    @Test
    void getExpiringProducts_verifyResponseStructure() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = createTestShoppingList(listId, "Test List");
        
        addProductToList(list, "Test Product", "Test Category", "Test Brand",
                LocalDate.now().plusDays(5), "User123", "User456", false);

        // Perform request and get the JSON response
        MvcResult result = mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andReturn();
        
        String content = result.getResponse().getContentAsString();
        List<Map<String, Object>> responseList = objectMapper.readValue(content, List.class);
        
        assertEquals(1, responseList.size());
        Map<String, Object> responseProduct = responseList.get(0);
        
        // Verify all expected fields are present
        assertNotNull(responseProduct.get("id"));
        assertEquals("Test Product", responseProduct.get("name"));
        assertEquals("Test Category", responseProduct.get("category"));
        assertEquals("Test Brand", responseProduct.get("brand"));
        assertNotNull(responseProduct.get("expiration"));
        assertEquals("expiring", responseProduct.get("status"));
    }

    @Test
    void getExpiringProducts_multipleListsWithSameProductNames_onlyReturnsProductsFromRequestedList() throws Exception {
        // Create first list
        UUID listId1 = UUID.randomUUID();
        ShoppingList list1 = createTestShoppingList(listId1, "List 1");
        
        // Create second list with same product name but different expiration
        UUID listId2 = UUID.randomUUID();
        ShoppingList list2 = createTestShoppingList(listId2, "List 2");
        
        // Add product to first list
        addProductToList(list1, "Milk", "Dairy", "Brand X", 
                LocalDate.now().minusDays(1), null, null, false);
        
        // Add product to second list
        addProductToList(list2, "Milk", "Dairy", "Brand X", 
                LocalDate.now().plusDays(10), null, null, false);

        // Check first list - should have expired product
        mockMvc.perform(get("/shoppinglists/" + listId1 + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[?(@.name=='Milk')].status").value("expired"));
        
        // Check second list - should have ok product
        mockMvc.perform(get("/shoppinglists/" + listId2 + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[?(@.name=='Milk')].status").value("ok"));
    }
} 