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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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

    @Test
    void getExpiringProducts_statusClassification() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = new ShoppingList(listId);

        // Abgelaufen: gestern
        Product expired = new Product("Joghurt", "Milchprodukte", "Gut&Günstig",
                new ExpirationDate(LocalDate.now().minusDays(1)));
        // Nahend: in 3 Tagen
        Product expiring = new Product("Milch", "Milchprodukte", "Weihenstephan",
                new ExpirationDate(LocalDate.now().plusDays(3)));
        // Ok: in 10 Tagen
        Product ok = new Product("Käse", "Milchprodukte", "Alpenhain",
                new ExpirationDate(LocalDate.now().plusDays(10)));

        list.addProduct(expired);
        list.addProduct(expiring);
        list.addProduct(ok);
        shoppingListRepository.safe(list);

        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name=='Joghurt')].status").value("expired"))
                .andExpect(jsonPath("$[?(@.name=='Milch')].status").value("expiring"))
                .andExpect(jsonPath("$[?(@.name=='Käse')].status").value("ok"));
    }

    @Test
    void getExpiringProducts_emptyList_returnsEmptyArray() throws Exception {
        UUID listId = UUID.randomUUID();
        ShoppingList list = new ShoppingList(listId);
        shoppingListRepository.safe(list);

        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getExpiringProducts_listNotFound_returns404() throws Exception {
        UUID listId = UUID.randomUUID();
        mockMvc.perform(get("/shoppinglists/" + listId + "/expiring-products"))
                .andExpect(status().isNotFound());
    }

} 