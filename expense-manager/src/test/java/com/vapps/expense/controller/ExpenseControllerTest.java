package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.config.EnableMongoTestServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.vapps.expense.controller.ControllerTestUtil.createFamily;
import static com.vapps.expense.controller.ControllerTestUtil.createUser;

@WebMvcTest(controllers = { ExpenseController.class })
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
    public void testAddExpense() throws Exception {

    }
}
