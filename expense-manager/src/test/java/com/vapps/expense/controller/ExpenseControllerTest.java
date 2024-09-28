package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.dto.response.ExpenseResponse;
import com.vapps.expense.common.dto.response.ExpensesResponse;
import com.vapps.expense.common.util.Endpoints;
import com.vapps.expense.config.EnableMongoTestServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static com.vapps.expense.controller.ControllerTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = { ExpenseController.class })
@AutoConfigureMockMvc
@EnableMongoTestServer
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExpenseControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private static String familyId;

	private static String personalExpenseId;

	private static String familyExpenseId;

	private static String personalCategoryId;

	private static String familyCategoryId;

	@BeforeEach
	public void setup() throws Exception {
		createUser(mockMvc, objectMapper, "user", "testuser");
		createUser(mockMvc, objectMapper, "another", "testuseranother");
		if (familyId == null) {
			familyId = createFamily(mockMvc, objectMapper, "user", "Testing Family", FamilyDTO.Visibility.PRIVATE);
		}
		if (personalCategoryId == null) {
			personalCategoryId = addCategory(mockMvc, objectMapper, "Taxi", "For office", "test", "user",
					"user", CategoryDTO.CategoryType.PERSONAL);
		}
		if (familyCategoryId == null) {
			familyCategoryId = addCategory(mockMvc, objectMapper, "Family Taxi", "For family trip", "test", familyId,
					"user", CategoryDTO.CategoryType.FAMILY);
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
		String currency = "USD";
		long amount = 70;
		ExpenseDTO expense = createExpense(name, description, type, time, amount, currency, null, personalCategoryId);
		assertThat(expense.getFamily()).isNull();
		assertThat(expense.getOwnerId()).isEqualTo("user");

		personalExpenseId = expense.getId();
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
		ExpenseDTO expense = createExpense(name, description, type, time, amount, currency, familyId, familyCategoryId);
		assertThat(expense.getFamily().getId()).isNotEqualTo(familyId);
		assertThat(expense.getFamily().getId()).isEqualTo(ExpenseControllerTest.familyId);
		assertThat(expense.getOwnerId()).isEqualTo(ExpenseControllerTest.familyId);

		familyExpenseId = expense.getId();
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
		ExpenseDTO expense = createExpense(name, description, type, time, amount, currency, familyId, familyCategoryId);
		assertThat(expense.getFamily().getId()).isEqualTo(ExpenseControllerTest.familyId);
		assertThat(expense.getOwnerId()).isEqualTo("user");
	}

	@Test
	@Order(4)
	@WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.Expense.UPDATE")
	public void testUpdatePersonalExpense() throws Exception {
		updateExpense(personalExpenseId, "Updated Personal", "Updated personal expense Description",
				LocalDateTime.now(), 7890, "INR", personalCategoryId);
	}

	@Test
	@Order(5)
	@WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.Expense.UPDATE")
	public void testUpdateFamilyExpense() throws Exception {
		updateExpense(familyExpenseId, "Updated Family", "Updated family expense Description", LocalDateTime.now(),
				8000, "USD", familyCategoryId);
	}

	@Test
	@Order(6)
	@WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.Expense.READ")
	public void testGetPersonalExpense() throws Exception {
		getExpense(personalExpenseId);
	}

	@Test
	@Order(7)
	@WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.Expense.READ")
	public void testGetFamilyExpense() throws Exception {
		getExpense(familyExpenseId);
	}

	@Test
	@Order(8)
	@WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.Expense.DELETE")
	public void testDeletePersonalExpense() throws Exception {
		deleteExpense(personalExpenseId);
	}

	@Test
	@Order(9)
	@WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.Expense.DELETE")
	public void testDeleteFamilyExpense() throws Exception {
		deleteExpense(familyExpenseId);
	}

	@Test
	@Order(10)
	@WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.Expense.READ",
			"SCOPE_ExpenseManager.Expense.CREATE" })
	public void testGetAllExpense() throws Exception {
		String currentPersonalCategoryId = personalCategoryId;
		String currentFamilyCategoryId = familyCategoryId;
		LocalDateTime jan2024 = LocalDateTime.of(2024, 1, 1, 1, 1);
		LocalDateTime feb2024 = LocalDateTime.of(2024, 2, 1, 1, 1);
		for (int i = 0; i < 50; i++) {
			ExpenseDTO.ExpenseType type = i % 2 == 0 ? ExpenseDTO.ExpenseType.PERSONAL : ExpenseDTO.ExpenseType.FAMILY;
			String expenseFamilyId = type == ExpenseDTO.ExpenseType.FAMILY ? familyId : null;
			LocalDateTime time = i > 20 ? feb2024 : jan2024;

			createExpense("Testing " + i, "Testing description " + i, type, time, 100, "INR",
					expenseFamilyId,
					type == ExpenseDTO.ExpenseType.FAMILY ? currentFamilyCategoryId : currentPersonalCategoryId);
			if (i % 5 == 0) {
				currentFamilyCategoryId = addCategory(mockMvc, objectMapper, "Testing Family Category" + i, "test",
						"https://test.com/image",
						familyId, "user", CategoryDTO.CategoryType.FAMILY);
				currentPersonalCategoryId = addCategory(mockMvc, objectMapper, "Testing Personal Category" + i, "test",
						"https://test.com/image",
						"user", "user", CategoryDTO.CategoryType.PERSONAL);
			}
		}

		// Test get all expenses
		MvcResult result = mockMvc.perform(get(Endpoints.GET_ALL_EXPENSES).param("isFamily", String.valueOf(false)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.expenses").exists()).andReturn();

		ExpensesResponse response = objectMapper.readValue(result.getResponse().getContentAsString(),
				ExpensesResponse.class);
		assertThat(response.getExpenses().size()).isGreaterThan(20);

		// Test get family expenses
		result = mockMvc.perform(get(Endpoints.GET_ALL_EXPENSES).param("isFamily", String.valueOf(true)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.expenses").exists()).andReturn();

		response = objectMapper.readValue(result.getResponse().getContentAsString(),
				ExpensesResponse.class);
		for (ExpenseDTO expense : response.getExpenses()) {
			assertThat(expense.getFamily()).isNotNull();
		}
		// Test with query
		result = mockMvc.perform(
						get(Endpoints.GET_ALL_EXPENSES).param("isFamily", String.valueOf(false)).param("query", "es")
								.param("searchBy",
										ExpenseFilter.SearchBy.NAME.name())
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.expenses").exists()).andReturn();

		response = objectMapper.readValue(result.getResponse().getContentAsString(),
				ExpensesResponse.class);
		for (ExpenseDTO expense : response.getExpenses()) {
			assertThat(expense.getName().toLowerCase().contains("es"));
		}

		// Test with category name
		result = mockMvc.perform(
						get(Endpoints.GET_ALL_EXPENSES).param("isFamily", String.valueOf(false)).param("query", "category")
								.param("searchBy",
										ExpenseFilter.SearchBy.CATEGORY.name())).andExpect(status().isOk())
				.andExpect(jsonPath("$.expenses").exists()).andReturn();

		response = objectMapper.readValue(result.getResponse().getContentAsString(),
				ExpensesResponse.class);
		for (ExpenseDTO expense : response.getExpenses()) {
			assertThat(expense.getCategory().getName().toLowerCase().contains("category"));
		}

		// Test with date range
		// From jan 15 2024 to feb 15 2024
		LocalDateTime start = LocalDateTime.of(2024, 1, 15, 1, 1);
		LocalDateTime end = LocalDateTime.of(2024, 2, 15, 1, 1);

		result = mockMvc.perform(
						get(Endpoints.GET_ALL_EXPENSES).param("isFamily", String.valueOf(false))
								.param("start", start.toString()).param("end", end.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.expenses").exists()).andReturn();

		response = objectMapper.readValue(result.getResponse().getContentAsString(),
				ExpensesResponse.class);
		for (ExpenseDTO expense : response.getExpenses()) {
			assertThat(expense.getTime()).isAfter(start).isBefore(end);
		}
	}

	private ExpenseDTO getExpense(String expenseId) throws Exception {

		MvcResult result = mockMvc.perform(
						get(UriComponentsBuilder.fromPath(Endpoints.GET_EXPENSE).buildAndExpand(expenseId).toUriString()))
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value())).andExpect(jsonPath("$.expense").exists())
				.andReturn();
		ExpenseResponse response =
				objectMapper.readValue(result.getResponse().getContentAsString(), ExpenseResponse.class);
		ExpenseDTO expense = response.getExpense();
		assertThat(expense.getId()).isEqualTo(expenseId);
		return expense;
	}

	private void deleteExpense(String expenseId) throws Exception {

		MvcResult result = mockMvc.perform(
						delete(UriComponentsBuilder.fromPath(Endpoints.DELETE_EXPENSE).buildAndExpand(expenseId).toUriString()))
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value())).andReturn();
		ExpenseResponse response =
				objectMapper.readValue(result.getResponse().getContentAsString(), ExpenseResponse.class);
	}

	private ExpenseDTO updateExpense(String expenseId, String name, String description, LocalDateTime time, long amount,
			String currency, String categoryId) throws Exception {
		ExpenseUpdatePayload payload = new ExpenseUpdatePayload();
		payload.setAmount(amount);
		payload.setCurrency(currency);
		payload.setTime(time);
		payload.setName(name);
		payload.setDescription(description);
		payload.setCategoryId(categoryId);

		MockMultipartFile payloadPart = new MockMultipartFile("payload", "payload",
				MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(payload)
				.getBytes(StandardCharsets.UTF_8));

		MvcResult result = mockMvc.perform(
						multipart(UriComponentsBuilder.fromPath(Endpoints.UPDATE_EXPENSE).buildAndExpand(expenseId)
								.toUriString())
								.file(payloadPart)
								.with(request -> {
									request.setMethod(HttpMethod.PATCH.name());
									return request;
								}))
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value())).andExpect(jsonPath("$.expense").exists())
				.andReturn();

		ExpenseResponse response =
				objectMapper.readValue(result.getResponse().getContentAsString(), ExpenseResponse.class);
		ExpenseDTO expense = response.getExpense();
		assertThat(expense.getId()).isEqualTo(expenseId);
		assertThat(expense.getName()).isEqualTo(name);
		assertThat(expense.getDescription()).isEqualTo(description);
		assertThat(expense.getTime()).isEqualTo(time);
		assertThat(expense.getAmount()).isEqualTo(amount);
		assertThat(expense.getCurrency()).isEqualTo(currency);
		assertThat(expense.getCategory().getId()).isEqualTo(categoryId);

		return expense;
	}

	private ExpenseDTO createExpense(String name, String description, ExpenseDTO.ExpenseType type, LocalDateTime time,
			long amount, String currency, String familyId, String categoryId) throws Exception {
		ExpenseCreationPayload payload = new ExpenseCreationPayload();

		payload.setDescription(description);
		payload.setType(type);
		payload.setTime(time);
		payload.setAmount(amount);
		payload.setCurrency(currency);
		payload.setFamilyId(familyId);
		payload.setName(name);
		payload.setCategoryId(categoryId);

		MockMultipartFile payloadPart = new MockMultipartFile("payload", "payload",
				MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(payload)
				.getBytes(StandardCharsets.UTF_8));

		MvcResult result = mockMvc.perform(multipart(Endpoints.CREATE_EXPENSE)
						.file(payloadPart).contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isOk())
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
		assertThat(expense.getCategory()).isNotNull();
		assertThat(expense.getCategory().getId()).isEqualTo(categoryId);

		return expense;
	}
}
