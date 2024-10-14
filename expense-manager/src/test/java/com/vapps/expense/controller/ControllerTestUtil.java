package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.dto.response.CategoryResponse;
import com.vapps.expense.common.dto.response.ExpenseResponse;
import com.vapps.expense.common.dto.response.FamilyResponse;
import com.vapps.expense.common.dto.response.UserResponse;
import com.vapps.expense.common.util.Endpoints;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static com.vapps.expense.util.TestUtil.getOidcUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ControllerTestUtil {

	public static String createUser(MockMvc mockMvc, ObjectMapper objectMapper, String userId, String userName)
			throws Exception {

		MvcResult mvcResult = mockMvc.perform(
						get(UriComponentsBuilder.fromPath(Endpoints.GET_USER).buildAndExpand(userId).toUriString()).with(
								oidcLogin().oidcUser(getOidcUser(userId, List.of("SCOPE_ExpenseManager.User.READ")))))
				.andReturn();

		UserResponse response =
				objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
		if (response.getUser() != null) {
			return response.getUser().getId();
		}

		UserDTO userDTO = new UserDTO();
		userDTO.setId(userId);
		userDTO.setAge(19);
		userDTO.setEmail(userName + "@gmail.com");
		userDTO.setImage("https://vapps.images.com/vicky/profile");
		userDTO.setName(userName);
		userDTO.setFirstName(userName);
		userDTO.setLastName("M");

		mvcResult = mockMvc.perform(post(Endpoints.CREATE_USER).with(
								oidcLogin().oidcUser(getOidcUser(userId, List.of("SCOPE_ExpenseManager.User.CREATE"))))
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userDTO)))
				.andExpect(status().isOk()).andReturn();
		UserResponse userResponse =
				objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
		assertThat(userResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(userResponse.getUser()).isEqualTo(userDTO);
		return userResponse.getUser().getId();
	}

	public static String createFamily(MockMvc mockMvc, ObjectMapper objectMapper, String userId, String familyName,
			FamilyDTO.Visibility visibility) throws Exception {
		String description = "Testing family";

		FamilyControllerTest.FamilyCreationPayload familyDTO = new FamilyControllerTest.FamilyCreationPayload();
		familyDTO.setDescription(description);
		familyDTO.setVisibility(visibility);
		familyDTO.setName(familyName);
		familyDTO.setImage("/testing.png");
		familyDTO.setJoinType(FamilyDTO.JoinType.ANYONE);

		MvcResult mvcResult = mockMvc.perform(post(Endpoints.CREATE_FAMILY).with(
								oidcLogin().oidcUser(getOidcUser(userId, List.of("SCOPE_ExpenseManager.Family" +
										".CREATE"))))
						.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(familyDTO)))
				.andExpect(status().isOk()).andReturn();

		FamilyResponse familyResponse =
				objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FamilyResponse.class);
		assertThat(familyResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		FamilyDTO family = familyResponse.getFamily();
		assertThat(family.getId()).isNotNull().isNotBlank();
		assertThat(family.getCreatedTime()).isNotNull();
		assertThat(family.getName()).isEqualTo(familyName);
		assertThat(family.getVisibility()).isEqualTo(visibility);
		assertThat(family.getDescription()).isEqualTo(description);
		assertThat(family.getJoinType()).isEqualTo(FamilyDTO.JoinType.ANYONE);

		return family.getId();
	}

	public static FamilyControllerTest.FamilyCreationPayload getFamilyCreationPayload(FamilyResponse response) {
		FamilyDTO familyDTO = response.getFamily();

		String updatedName = familyDTO.getName();
		String updatedDescription = familyDTO.getDescription();
		FamilyDTO.Visibility updatedVisibility = FamilyDTO.Visibility.PUBLIC;
		String updateImage = familyDTO.getImage();

		FamilyControllerTest.FamilyCreationPayload payload = new FamilyControllerTest.FamilyCreationPayload();
		payload.setName(updatedName);
		payload.setDescription(updatedDescription);
		payload.setImage(updateImage);
		payload.setVisibility(updatedVisibility);
		payload.setJoinType(FamilyDTO.JoinType.ANYONE);
		return payload;
	}

	public static String addCategory(MockMvc mockMvc, ObjectMapper objectMapper, String name, String description,
			String image, String ownerId, String userId, CategoryDTO.CategoryType type) throws Exception {

		CategoryControllerTest.CategoryCreationPayload payload = new CategoryControllerTest.CategoryCreationPayload();
		payload.setName(name);
		payload.setDescription(description);
		payload.setImage(image);
		payload.setType(type);
		payload.setOwnerId(ownerId);

		MvcResult result = mockMvc.perform(post(Endpoints.CREATE_CATEGORY).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload))
						.with(oidcLogin().oidcUser(getOidcUser(userId, List.of("SCOPE_ExpenseManager.Category.CREATE")))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value())).andExpect(jsonPath("$.category").exists())
				.andReturn();

		CategoryResponse response =
				objectMapper.readValue(result.getResponse().getContentAsString(), CategoryResponse.class);
		CategoryDTO category = response.getCategory();
		assertThat(category.getId()).isNotNull();
		assertThat(category.getType()).isEqualTo(type);
		assertThat(category.getName()).isEqualTo(name);
		assertThat(category.getDescription()).isEqualTo(description);
		assertThat(category.getImage()).isEqualTo(image);
		assertThat(category.getCreatedBy().getId()).isEqualTo(userId);
		assertThat(category.getOwnerId()).isEqualTo(ownerId);

		return response.getCategory().getId();
	}

	public static ExpenseDTO createExpense(MockMvc mockMvc, ObjectMapper objectMapper, String userId, String name,
			String description, ExpenseDTO.ExpenseType type, LocalDateTime time,
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
						.file(payloadPart).contentType(MediaType.MULTIPART_FORM_DATA)
						.with(oidcLogin().oidcUser(getOidcUser(userId, List.of("SCOPE_ExpenseManager.Expense.CREATE")))))
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
		if (categoryId != null) {
			assertThat(expense.getCategory()).isNotNull();
			assertThat(expense.getCategory().getId()).isEqualTo(categoryId);
		}

		return expense;
	}
}
