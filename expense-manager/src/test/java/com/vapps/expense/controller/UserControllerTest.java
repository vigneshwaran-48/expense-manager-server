package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.dto.response.AppErrorResponse;
import com.vapps.expense.common.dto.response.UserResponse;
import com.vapps.expense.common.util.Endpoints;
import com.vapps.expense.config.EnableMongoTestServer;
import lombok.Data;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@EnableMongoTestServer
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerTest.class);

	//    @Test
	//    @Order(1)
	//    void shouldCreateUserFailForInvalidEmail() throws Exception {
	//        UserDTO userDTO = new UserDTO();
	//        userDTO.setId("testing_user_id");
	//        userDTO.setAge(19);
	//        userDTO.setEmail("vigneesomsomething");
	//        userDTO.setImage("https://vapps.images.com/vicky/profile");
	//        userDTO.setName("Vicky");
	//        userDTO.setFirstName("Vigneshwaran");
	//        userDTO.setLastName("M");
	//
	//        String userStr = objectMapper.writeValueAsString(userDTO);
	//
	//        MvcResult mvcResult = mockMvc.perform(post(Endpoints.CREATE_USER).with(
	//                                jwt().authorities(new SimpleGrantedAuthority("SCOPE_ExpenseManager.User.CREATE")))
	//                        .contentType(MediaType.APPLICATION_JSON).content(userStr)).andExpect(status().isBadRequest())
	//                .andReturn();
	//        AppErrorResponse userResponse =
	//                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AppErrorResponse.class);
	//        assertThat(userResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	//    }

	@Test
	@Order(2)
	void shouldCreateUserFailForAccessDenied() throws Exception {
		UserDTO userDTO = new UserDTO();
		userDTO.setId("testing_user_id");
		userDTO.setAge(19);
		userDTO.setEmail("vigneesomsomething");
		userDTO.setImage("https://vapps.images.com/vicky/profile");
		userDTO.setName("Vicky");
		userDTO.setFirstName("Vigneshwaran");
		userDTO.setLastName("M");

		String userStr = objectMapper.writeValueAsString(userDTO);

		OidcUser oidcUser = new DefaultOidcUser(AuthorityUtils.createAuthorityList(List.of()),
				OidcIdToken.withTokenValue("id-token").claim("sub", "testing_user_id").build(), "sub");

		MvcResult mvcResult = mockMvc.perform(
				post(Endpoints.CREATE_USER).with(oidcLogin().oidcUser(oidcUser)).contentType(MediaType.APPLICATION_JSON)
						.content(userStr)).andExpect(status().isForbidden()).andReturn();
		AppErrorResponse userResponse =
				objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AppErrorResponse.class);
		assertThat(userResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

	@Test
	@Order(3)
	void shouldCreateUserFailForAuthentication() throws Exception {
		UserDTO userDTO = new UserDTO();
		userDTO.setId("testing_user_id");
		userDTO.setAge(19);
		userDTO.setEmail("vigneesomsomething");
		userDTO.setImage("https://vapps.images.com/vicky/profile");
		userDTO.setName("Vicky");
		userDTO.setFirstName("Vigneshwaran");
		userDTO.setLastName("M");

		String userStr = objectMapper.writeValueAsString(userDTO);

		mockMvc.perform(post(Endpoints.CREATE_USER).contentType(MediaType.APPLICATION_JSON).content(userStr))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@Order(4)
	void shouldCreateUser() throws Exception {
		UserDTO userDTO = new UserDTO();
		userDTO.setId("testing_user_id");
		userDTO.setAge(19);
		userDTO.setEmail("vignesh@test.com");
		userDTO.setImage("https://vapps.images.com/vicky/profile");
		userDTO.setName("Vicky");
		userDTO.setFirstName("Vigneshwaran");
		userDTO.setLastName("M");

		String userStr = objectMapper.writeValueAsString(userDTO);

		OidcUser oidcUser = new DefaultOidcUser(AuthorityUtils.createAuthorityList("SCOPE_ExpenseManager.User.CREATE"),
				OidcIdToken.withTokenValue("id-token").claim("sub", "testing_user_id").build(), "sub");

		MvcResult mvcResult = mockMvc.perform(
				post(Endpoints.CREATE_USER).with(oidcLogin().oidcUser(oidcUser)).contentType(MediaType.APPLICATION_JSON)
						.content(userStr)).andExpect(status().isOk()).andReturn();
		UserResponse userResponse =
				objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);

		assertThat(userResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(userResponse.getUser().getId()).isEqualTo(userDTO.getId());
		assertThat(userResponse.getUser().getName()).isEqualTo(userDTO.getName());
		assertThat(userResponse.getUser().getFirstName()).isEqualTo(userDTO.getFirstName());
		assertThat(userResponse.getUser().getLastName()).isEqualTo(userDTO.getLastName());
		assertThat(userResponse.getUser().getAge()).isEqualTo(userDTO.getAge());
		assertThat(userResponse.getUser().getEmail()).isEqualTo(userDTO.getEmail());
		assertThat(userResponse.getUser().getImage()).isEqualTo(userDTO.getImage());
	}

	@Test
	@Order(5)
	void shouldCreateAnotherUser() throws Exception {
		UserDTO userDTO = new UserDTO();
		userDTO.setId("different_user_id");
		userDTO.setAge(19);
		userDTO.setEmail("different@test.com");
		userDTO.setImage("https://vapps.images.com/vicky/profile");
		userDTO.setName("Different");
		userDTO.setFirstName("Different");
		userDTO.setLastName("M");

		String userStr = objectMapper.writeValueAsString(userDTO);

		OidcUser oidcUser = new DefaultOidcUser(AuthorityUtils.createAuthorityList("SCOPE_ExpenseManager.User.CREATE"),
				OidcIdToken.withTokenValue("id-token").claim("sub", "different_user_id").build(), "sub");

		MvcResult mvcResult = mockMvc.perform(
				post(Endpoints.CREATE_USER).with(oidcLogin().oidcUser(oidcUser)).contentType(MediaType.APPLICATION_JSON)
						.content(userStr)).andExpect(status().isOk()).andReturn();
		UserResponse userResponse =
				objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);

		assertThat(userResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(userResponse.getUser().getId()).isEqualTo(userDTO.getId());
		assertThat(userResponse.getUser().getName()).isEqualTo(userDTO.getName());
		assertThat(userResponse.getUser().getFirstName()).isEqualTo(userDTO.getFirstName());
		assertThat(userResponse.getUser().getLastName()).isEqualTo(userDTO.getLastName());
		assertThat(userResponse.getUser().getAge()).isEqualTo(userDTO.getAge());
		assertThat(userResponse.getUser().getEmail()).isEqualTo(userDTO.getEmail());
		assertThat(userResponse.getUser().getImage()).isEqualTo(userDTO.getImage());
	}

	@Test
	@Order(6)
	void shouldUpdateUserFailForDifferentUser() throws Exception {

		OidcUser oidcUser = new DefaultOidcUser(AuthorityUtils.createAuthorityList("SCOPE_ExpenseManager.User.READ"),
				OidcIdToken.withTokenValue("id-token").claim("sub", "testing_user_id").build(), "sub");

		MvcResult getUserResult = mockMvc.perform(
				get(UriComponentsBuilder.fromPath(Endpoints.GET_USER).buildAndExpand("testing_user_id")
						.toUriString()).with(oidcLogin().oidcUser(oidcUser))).andExpect(status().isOk()).andReturn();
		UserResponse getResponse =
				objectMapper.readValue(getUserResult.getResponse().getContentAsString(), UserResponse.class);
		assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(getResponse.getUser()).isNotNull();
		assertThat(getResponse.getUser().getId()).isEqualTo("testing_user_id");

		int updateAged = getResponse.getUser().getAge() + 5;
		String updatedName = getResponse.getUser().getName() + "_updated";
		String updatedFirstName = getResponse.getUser().getFirstName() + "_updated";
		String updatedLastName = getResponse.getUser().getLastName() + "_updated";

		UpdateUserDTO userDTO = new UpdateUserDTO();
		userDTO.setAge(updateAged);
		userDTO.setName(updatedName);
		userDTO.setFirstName(updatedFirstName);
		userDTO.setLastName(updatedLastName);

		String userStr = objectMapper.writeValueAsString(userDTO);

		oidcUser = new DefaultOidcUser(AuthorityUtils.createAuthorityList("SCOPE_ExpenseManager.User.UPDATE"),
				OidcIdToken.withTokenValue("id-token").claim("sub", "different_user_id").build(), "sub");

		MvcResult mvcResult = mockMvc.perform(
						patch(UriComponentsBuilder.fromPath(Endpoints.UPDATE_USER).buildAndExpand("testing_user_id")
								.toUriString()).with(user("testing_user_id")).with(oidcLogin().oidcUser(oidcUser))
								.contentType(MediaType.APPLICATION_JSON).content(userStr)).andExpect(status().isForbidden())
				.andReturn();
		AppErrorResponse errorResponse =
				objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AppErrorResponse.class);
		assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
	}

	@Test
	@Order(7)
	void shouldUpdateUser() throws Exception {

		OidcUser oidcUser = new DefaultOidcUser(AuthorityUtils.createAuthorityList("SCOPE_ExpenseManager.User.READ"),
				OidcIdToken.withTokenValue("id-token").claim("sub", "testing_user_id").build(), "sub");

		MvcResult getUserResult = mockMvc.perform(
				get(UriComponentsBuilder.fromPath(Endpoints.GET_USER).buildAndExpand("testing_user_id")
						.toUriString()).with(oidcLogin().oidcUser(oidcUser))).andExpect(status().isOk()).andReturn();
		UserResponse getResponse =
				objectMapper.readValue(getUserResult.getResponse().getContentAsString(), UserResponse.class);
		assertThat(getResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(getResponse.getUser()).isNotNull();
		assertThat(getResponse.getUser().getId()).isEqualTo("testing_user_id");

		int updateAged = getResponse.getUser().getAge() + 5;
		String updatedName = getResponse.getUser().getName() + "_updated";
		String updatedFirstName = getResponse.getUser().getFirstName() + "_updated";
		String updatedLastName = getResponse.getUser().getLastName() + "_updated";

		UpdateUserDTO userDTO = new UpdateUserDTO();
		userDTO.setAge(updateAged);
		userDTO.setName(updatedName);
		userDTO.setFirstName(updatedFirstName);
		userDTO.setLastName(updatedLastName);

		String userStr = objectMapper.writeValueAsString(userDTO);

		oidcUser = new DefaultOidcUser(AuthorityUtils.createAuthorityList("SCOPE_ExpenseManager.User.UPDATE"),
				OidcIdToken.withTokenValue("id-token").claim("sub", "testing_user_id").build(), "sub");

		MvcResult mvcResult = mockMvc.perform(
						patch(UriComponentsBuilder.fromPath(Endpoints.UPDATE_USER).buildAndExpand("testing_user_id")
								.toUriString()).with(user("testing_user_id")).with(oidcLogin().oidcUser(oidcUser))
								.contentType(MediaType.APPLICATION_JSON).content(userStr)).andExpect(status().isOk())
				.andReturn();
		UserResponse userResponse =
				objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
		assertThat(userResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(userResponse.getUser().getAge()).isEqualTo(updateAged);
		assertThat(userResponse.getUser().getName()).isEqualTo(updatedName);
		assertThat(userResponse.getUser().getFirstName()).isEqualTo(updatedFirstName);
		assertThat(userResponse.getUser().getLastName()).isEqualTo(updatedLastName);
	}

	@Data
	private static class UpdateUserDTO {
		private String name;
		private String firstName;
		private String lastName;
		private int age;
	}
}
