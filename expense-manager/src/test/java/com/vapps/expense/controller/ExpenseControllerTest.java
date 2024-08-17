package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.ExpenseCreationPayload;
import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.response.ExpenseResponse;
import com.vapps.expense.common.util.Endpoints;
import com.vapps.expense.config.EnableMongoTestServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

import static com.vapps.expense.controller.ControllerTestUtil.createFamily;
import static com.vapps.expense.controller.ControllerTestUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ExpenseController.class})
@AutoConfigureMockMvc
@EnableMongoTestServer
@EnableMongoRepositories(basePackages = "com.vapps.expense.repository.mongo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExpenseControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private static String familyId;

    @BeforeEach
    public void setup() throws Exception {
        createUser(mockMvc, objectMapper, "user", "testuser");
        createUser(mockMvc, objectMapper, "another", "testuseranother");
        if (familyId == null) {
            familyId = createFamily(mockMvc, objectMapper, "user", "Testing Family", FamilyDTO.Visibility.PRIVATE);
        }
    }

    @Test
    @Order(1)
    @WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.Expense.CREATE")
    public void testAddPersonalExpense() throws Exception {
        String name = "Testing";
        String description = "Testing description";
        ExpenseDTO.ExpenseType type = ExpenseDTO.ExpenseType.PERSONAL;
        LocalDateTime time = LocalDateTime.now();
        String familyId = null;
        String currency = "USD";
        long amount = 70;
        ExpenseDTO expense = createExpense(name, description, type, time, amount, currency, familyId);
        assertThat(expense.getFamily()).isNull();
        assertThat(expense.getOwnerId()).isEqualTo("user");
    }

    @Test
    @Order(2)
    @WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.Expense.CREATE")
    public void testAddFamilyExpense() throws Exception {
        String name = "Family expense";
        String description = "Testing Family expense description";
        ExpenseDTO.ExpenseType type = ExpenseDTO.ExpenseType.FAMILY;
        LocalDateTime time = LocalDateTime.now();
        String familyId = "this_will_be_ignored_for_family";
        String currency = "USD";
        long amount = 70;
        ExpenseDTO expense = createExpense(name, description, type, time, amount, currency, familyId);
        assertThat(expense.getFamily().getId()).isNotEqualTo(familyId);
        assertThat(expense.getFamily().getId()).isEqualTo(ExpenseControllerTest.familyId);
        assertThat(expense.getOwnerId()).isEqualTo(ExpenseControllerTest.familyId);
    }

    @Test
    @Order(3)
    @WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.Expense.CREATE")
    public void testAddPersonalExpenseInFamily() throws Exception {
        String name = "Personal expense F";
        String description = "Personal Expense in a family!";
        ExpenseDTO.ExpenseType type = ExpenseDTO.ExpenseType.PERSONAL;
        LocalDateTime time = LocalDateTime.now();
        String currency = "USD";
        long amount = 70;
        ExpenseDTO expense = createExpense(name, description, type, time, amount, currency, familyId);
        assertThat(expense.getFamily().getId()).isEqualTo(ExpenseControllerTest.familyId);
        assertThat(expense.getOwnerId()).isEqualTo("user");
    }

    private ExpenseDTO createExpense(String name, String description, ExpenseDTO.ExpenseType type, LocalDateTime time,
                                     long amount, String currency, String familyId) throws Exception {
        ExpenseCreationPayload payload = new ExpenseCreationPayload();

        payload.setDescription(description);
        payload.setType(type);
        payload.setTime(time);
        payload.setAmount(amount);
        payload.setCurrency(currency);
        payload.setFamilyId(familyId);
        payload.setName(name);

        MvcResult result = mockMvc.perform(post(Endpoints.CREATE_EXPENSE).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.expense").exists()).andReturn();

        ExpenseResponse response =
                objectMapper.readValue(result.getResponse().getContentAsString(), ExpenseResponse.class);
        ExpenseDTO expense = response.getExpense();
        assertThat(expense.getType()).isEqualTo(type);
        assertThat(expense.getTime()).isEqualTo(time);
        assertThat(expense.getId()).isNotNull();
        assertThat(expense.getAmount()).isEqualTo(amount);
        assertThat(expense.getCurrency()).isEqualTo(currency);
        assertThat(expense.getDescription()).isEqualTo(description);
        assertThat(expense.getName()).isEqualTo(name);

        return expense;
    }
}
