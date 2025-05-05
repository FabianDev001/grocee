package de.fab001.grocee;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fab001.grocee.domain.model.ExpirationDate;
import de.fab001.grocee.domain.model.Product;
import de.fab001.grocee.domain.model.ShoppingList;
import de.fab001.grocee.adapters.InMemoryShoppingListRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ShoppingListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InMemoryShoppingListRepository shoppingListRepository;

    @Test 
    void shouldAlwaysBeTrue(){
        assertTrue(true);
    }

    @Test
    void addProductToList_success() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = new ShoppingList(listId);
        shoppingListRepository.safe(list);

        Product product = new Product("Banana", "Fruit", "Chiquita", new ExpirationDate(LocalDate.now().plusDays(5)));

        mockMvc.perform(post("/shoppinglists/" + listId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated());
    }

    // Optional: Test für doppelten Produktnamen (setzt voraus, dass die Liste persistiert oder gemockt wird)
} 