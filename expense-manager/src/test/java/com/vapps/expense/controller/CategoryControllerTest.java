package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.common.dto.response.CategoriesResponse;
import com.vapps.expense.common.dto.response.CategoryResponse;
import com.vapps.expense.common.dto.response.InvitationsResponse;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.util.Endpoints;
import com.vapps.expense.config.EnableMongoTestServer;
import lombok.Data;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.vapps.expense.controller.ControllerTestUtil.*;
import static com.vapps.expense.util.TestUtil.getOidcUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(controllers = { CategoryController.class })
@AutoConfigureMockMvc
@EnableMongoTestServer
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final Logger LOGGER = LoggerFactory.getLogger(FamilyControllerTest.class);

	private static String personalCategoryId;
	private static String familyCategoryId;
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
	@WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.Category.CREATE" })
	public void testAddPersonalCategory() throws Exception {

		String name = "Testing category";
		String description = "Testing description with a long a string  rejgibj24bg24bg3kn24kn244kgln24";
		CategoryDTO.CategoryType type = CategoryDTO.CategoryType.PERSONAL;
		String image = "https://myimage.com/83574nrj";

		CategoryCreationPayload payload = new CategoryCreationPayload();
		payload.setName(name);
		payload.setDescription(description);
		payload.setImage(image);
		payload.setType(type);

		MvcResult result = mockMvc.perform(post(Endpoints.CREATE_CATEGORY).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload))).andExpect(status().isOk())
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
		assertThat(category.getOwnerId()).isEqualTo("user");
		assertThat(category.getCreatedBy().getId()).isEqualTo("user");

		personalCategoryId = category.getId();
	}

	@Test
	@Order(2)
	@WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.Category.CREATE" })
	public void testAddFamilyCategory() throws Exception {
		String name = "Family category";
		String description = "Family category description with a long a string  rejgibj24bg24bg3kn24kn244kgln24";
		CategoryDTO.CategoryType type = CategoryDTO.CategoryType.FAMILY;
		String image = "https://myimage.com/83574nrj/family";
		String ownerId = familyId;

		CategoryCreationPayload payload = new CategoryCreationPayload();
		payload.setName(name);
		payload.setDescription(description);
		payload.setImage(image);
		payload.setType(type);
		payload.setOwnerId(ownerId);

		MvcResult result = mockMvc.perform(post(Endpoints.CREATE_CATEGORY).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload))).andExpect(status().isOk())
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
		assertThat(category.getCreatedBy().getId()).isEqualTo("user");
		assertThat(category.getOwnerId()).isEqualTo(ownerId);

		familyCategoryId = category.getId();
	}

	@Test
	@Order(3)
	@WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.Category.UPDATE" })
	public void testUpdateCategory() throws Exception {
		String name = "Updated category";
		String description = "Updated category description with a long a string  rejgibj24bg24bg3kn24kn244kgln24";
		String image = "https://myimage.com/83574nrj/updated";

		CategoryUpdatePayload payload = new CategoryUpdatePayload();
		payload.setName(name);
		payload.setDescription(description);
		payload.setImage(image);

		MvcResult result = mockMvc.perform(
						patch(UriComponentsBuilder.fromPath(Endpoints.UPDATE_CATEGORY).buildAndExpand(personalCategoryId)
								.toUriString()).contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(payload))).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value())).andExpect(jsonPath("$.category").exists())
				.andReturn();

		CategoryResponse response =
				objectMapper.readValue(result.getResponse().getContentAsString(), CategoryResponse.class);
		CategoryDTO category = response.getCategory();
		assertThat(category.getId()).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
		assertThat(category.getDescription()).isEqualTo(description);
		assertThat(category.getImage()).isEqualTo(image);
		assertThat(category.getCreatedBy().getId()).isEqualTo("user");
		assertThat(category.getOwnerId()).isEqualTo("user");
	}

	@Test
	@Order(4)
	@WithMockUser(username = "another", authorities = { "SCOPE_ExpenseManager.Category.UPDATE" })
	public void testUpdateOtherFamilyCategory() throws Exception {
		String name = "Updated category by another";
		String description =
				"Updated another category description with a long a string rejgibj24bg24bg3kn24kn244kgln24";
		String image = "https://myimage.com/83574nrj/updated/another";

		CategoryUpdatePayload payload = new CategoryUpdatePayload();
		payload.setName(name);
		payload.setDescription(description);
		payload.setImage(image);

		mockMvc.perform(patch(UriComponentsBuilder.fromPath(Endpoints.UPDATE_CATEGORY).buildAndExpand(familyCategoryId)
						.toUriString()).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	@Order(5)
	@WithMockUser(username = "another", authorities = { "SCOPE_ExpenseManager.Category.CREATE" })
	public void testAddCategoryToFamilyByMemberRole() throws Exception {
		addFamilyMember("another");
		String name = "Member role category";
		String description = "member category description with a long a string  rejgibj24bg24bg3kn24kn244kgln24";
		CategoryDTO.CategoryType type = CategoryDTO.CategoryType.FAMILY;
		String image = "https://myimage.com/83574nrj/member";
		String ownerId = familyId;

		CategoryCreationPayload payload = new CategoryCreationPayload();
		payload.setName(name);
		payload.setDescription(description);
		payload.setImage(image);
		payload.setType(type);
		payload.setOwnerId(ownerId);

		mockMvc.perform(post(Endpoints.CREATE_CATEGORY).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload))).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
	}

	@Test
	@Order(6)
	@WithMockUser(username = "another", authorities = { "SCOPE_ExpenseManager.Category.UPDATE" })
	public void testUpdateFamilyCategoryByMemberRole() throws Exception {
		String name = "Updated role another";
		String description = "Updated role category description with a long a string rejgibj24bg24bg3kn24kn244kgln24";
		String image = "https://myimage.com/83574nrj/updated/role";

		CategoryUpdatePayload payload = new CategoryUpdatePayload();
		payload.setName(name);
		payload.setDescription(description);
		payload.setImage(image);

		mockMvc.perform(patch(UriComponentsBuilder.fromPath(Endpoints.UPDATE_CATEGORY).buildAndExpand(familyCategoryId)
						.toUriString()).contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(payload))).andExpect(status().isForbidden())
				.andExpect(jsonPath("$.status").value(HttpStatus.FORBIDDEN.value()));
	}

	@Test
	@Order(7)
	@WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.Category.DELETE" })
	public void testDeleteCategory() throws Exception {
		mockMvc.perform(
						delete(UriComponentsBuilder.fromPath(Endpoints.UPDATE_CATEGORY).buildAndExpand(personalCategoryId)
								.toUriString()).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));
	}

	@Test
	@Order(8)
	@WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.Category.DELETE" })
	public void testDeleteNonExistingCategory() throws Exception {
		mockMvc.perform(delete(UriComponentsBuilder.fromPath(Endpoints.UPDATE_CATEGORY).buildAndExpand("nonexisting")
						.toUriString()).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	@Order(9)
	@WithMockUser(username = "user34", authorities = { "SCOPE_ExpenseManager.Category.READ" })
	public void testGetAllCategories() throws Exception {

		createUser(mockMvc, objectMapper, "user34", "User34");

		addCategory(mockMvc, objectMapper, "test1", "test", "image", "user34", "user34",
				CategoryDTO.CategoryType.PERSONAL);
		addCategory(mockMvc, objectMapper, "test2", "test", "image", "user34", "user34",
				CategoryDTO.CategoryType.PERSONAL);
		addCategory(mockMvc, objectMapper, "test3", "test", "image", "user34", "user34",
				CategoryDTO.CategoryType.PERSONAL);
		addCategory(mockMvc, objectMapper, "test4", "test", "image", "user34", "user34",
				CategoryDTO.CategoryType.PERSONAL);

		String familyId = createFamily(mockMvc, objectMapper, "user34", "MyCategoryFamily",
				FamilyDTO.Visibility.PRIVATE);
		addCategory(mockMvc, objectMapper, "familytest", "test", "image", familyId, "user34",
				CategoryDTO.CategoryType.FAMILY);

		MvcResult result = mockMvc.perform(get(Endpoints.GET_ALL_CATEGORIES)).andExpect(status().isOk())
				.andExpect(jsonPath("$.categories").exists()).andReturn();
		CategoriesResponse response = objectMapper.readValue(result.getResponse().getContentAsString(),
				CategoriesResponse.class);
		assertThat(response.getCategories().size()).isEqualTo(5);
	}

	private void addFamilyMember(String member) throws Exception {
		MvcResult mvcResult = mockMvc.perform(
						post(UriComponentsBuilder.fromPath(Endpoints.INVITE_MEMBER).buildAndExpand(familyId, member)
								.toUriString()).param("role", FamilyMemberDTO.Role.MEMBER.name()).with(oidcLogin().oidcUser(
								getOidcUser("user", List.of("SCOPE_ExpenseManager.Family.Member.INVITE")))))
				.andExpect(status().isOk()).andReturn();
		Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

		mvcResult = mockMvc.perform(get(Endpoints.GET_ALL_INVITATIONS).with(
						oidcLogin().oidcUser(getOidcUser(member, List.of("SCOPE_ExpenseManager.Invitation.READ")))))
				.andExpect(status().isOk()).andReturn();

		InvitationsResponse invitationsResponse =
				objectMapper.readValue(mvcResult.getResponse().getContentAsString(), InvitationsResponse.class);
		assertThat(invitationsResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(invitationsResponse.getInvitations().size()).isGreaterThan(0);

		InvitationDTO invitation = invitationsResponse.getInvitations().get(0);
		mvcResult = mockMvc.perform(
						post(UriComponentsBuilder.fromPath(Endpoints.ACCEPT_INVITATION).buildAndExpand(invitation.getId())
								.toUriString()).with(oidcLogin().oidcUser(
								getOidcUser(member, List.of("SCOPE_ExpenseManager.Invitation" + ".ACCEPT")))))
				.andExpect(status().isOk()).andReturn();

		response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	}

	@Data
	static class CategoryCreationPayload {
		private String name;
		private String description;
		private CategoryDTO.CategoryType type;
		private String image;
		private String ownerId;
	}

	@Data
	static class CategoryUpdatePayload {
		private String name;
		private String description;
		private String image;
	}
}
