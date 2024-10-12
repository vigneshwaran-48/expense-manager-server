package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.dto.ExpenseStatsDTO;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.response.ExpenseStatsResponse;
import com.vapps.expense.common.util.Endpoints;
import com.vapps.expense.config.EnableMongoTestServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static com.vapps.expense.controller.ControllerTestUtil.*;
import static com.vapps.expense.controller.ControllerTestUtil.addCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = { StatsController.class })
@AutoConfigureMockMvc
@EnableMongoTestServer
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StatsControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private static String familyId;
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
	@WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.Family.READ")
	public void testDefaultFamilyStats() throws Exception {
		MvcResult result = mockMvc.perform(get(Endpoints.GET_FAMILY_STATS)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").exists()).andReturn();
		ExpenseStatsResponse response = objectMapper.readValue(result.getResponse().getContentAsString(),
				ExpenseStatsResponse.class);
		ExpenseStatsDTO stats = response.getStats();
		assertThat(stats.getId()).isNotNull();
		assertThat(stats.getType()).isEqualTo(ExpenseStatsDTO.ExpenseStatsType.FAMILY);
		assertThat(stats.getOwnerId()).isEqualTo(familyId);
		assertThat(stats.getRecentExpenses().size()).isEqualTo(0);
		assertThat(stats.getTopUsers().size()).isEqualTo(0);
		assertThat(stats.getTopCategories().size()).isEqualTo(0);
		assertThat(stats.getCurrentMonthTotal()).isEqualTo(0);
		assertThat(stats.getCurrentWeekTotal()).isEqualTo(0);
	}

	@Test
	@Order(2)
	@WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.User.READ")
	public void testDefaultPersonalStats() throws Exception {

		MvcResult result = mockMvc.perform(get(Endpoints.GET_PERSONAL_STATS)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").exists()).andReturn();
		ExpenseStatsResponse response = objectMapper.readValue(result.getResponse().getContentAsString(),
				ExpenseStatsResponse.class);
		ExpenseStatsDTO stats = response.getStats();
		assertThat(stats.getId()).isNotNull();
		assertThat(stats.getType()).isEqualTo(ExpenseStatsDTO.ExpenseStatsType.PERSONAL);
		assertThat(stats.getOwnerId()).isEqualTo("user");
		assertThat(stats.getRecentExpenses().size()).isEqualTo(0);
		assertThat(stats.getTopUsers().size()).isEqualTo(0);
		assertThat(stats.getTopCategories().size()).isEqualTo(0);
		assertThat(stats.getCurrentMonthTotal()).isEqualTo(0);
		assertThat(stats.getCurrentWeekTotal()).isEqualTo(0);
	}

	@Test
	@Order(3)
	@WithMockUser(username = "user", authorities = "SCOPE_ExpenseManager.FAMILY.READ")
	public void testRecentExpenseStats() throws Exception {

		createExpense(mockMvc, objectMapper, "user", "Recent Expense 1", "Recent expense description",
				ExpenseDTO.ExpenseType.PERSONAL, LocalDateTime.now(), 90, "IND", null, null);

		MvcResult result = mockMvc.perform(get(Endpoints.GET_PERSONAL_STATS)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").exists()).andReturn();
		ExpenseStatsResponse response = objectMapper.readValue(result.getResponse().getContentAsString(),
				ExpenseStatsResponse.class);
		ExpenseStatsDTO stats = response.getStats();
		assertThat(stats.getRecentExpenses().size()).isEqualTo(1);
		assertThat(stats.getRecentExpenses().get(0).getName()).isEqualTo("Recent Expense 1");


	}
}
