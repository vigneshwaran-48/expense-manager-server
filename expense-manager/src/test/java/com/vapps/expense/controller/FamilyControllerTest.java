package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.dto.response.FamilyResponse;
import com.vapps.expense.common.dto.response.InvitationsResponse;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.dto.response.UserResponse;
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
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.vapps.expense.util.TestUtil.getOidcUser;
import static com.vapps.expense.util.TestUtil.logTestCasePassed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = { FamilyController.class, InvitationController.class })
@AutoConfigureMockMvc
@EnableMongoTestServer
@EnableMongoRepositories(basePackages = "com.vapps.expense.repository.mongo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FamilyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String familyId;

    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyControllerTest.class);
    private static final String USER_ID = "testing_user_id";
    private static final String MEMBER_ID = "testing_member_id";

    @BeforeEach
    public void setup() throws Exception {

        OidcUser oidcUser = new DefaultOidcUser(AuthorityUtils.createAuthorityList("SCOPE_ExpenseManager.User.READ"),
                OidcIdToken.withTokenValue("id-token").claim("sub", USER_ID).build(), "sub");

        MvcResult getUserResult = mockMvc.perform(
                get(UriComponentsBuilder.fromPath(Endpoints.GET_USER).buildAndExpand("testing_user_id")
                        .toUriString()).with(oidcLogin().oidcUser(oidcUser))).andReturn();
        UserResponse userResponse =
                objectMapper.readValue(getUserResult.getResponse().getContentAsString(), UserResponse.class);

        if (userResponse.getStatus() == HttpStatus.OK.value()) {
            return;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(USER_ID);
        userDTO.setAge(19);
        userDTO.setEmail("vigneshwaran4817@gmail.com");
        userDTO.setImage("https://vapps.images.com/vicky/profile");
        userDTO.setName("Vicky");
        userDTO.setFirstName("Vigneshwaran");
        userDTO.setLastName("M");

        String userStr = objectMapper.writeValueAsString(userDTO);

        oidcUser = new DefaultOidcUser(AuthorityUtils.createAuthorityList("SCOPE_ExpenseManager.User.CREATE"),
                OidcIdToken.withTokenValue("id-token").claim("sub", "testing_user_id").build(), "sub");

        MvcResult mvcResult = mockMvc.perform(
                post(Endpoints.CREATE_USER).with(oidcLogin().oidcUser(oidcUser)).contentType(MediaType.APPLICATION_JSON)
                        .content(userStr)).andExpect(status().isOk()).andReturn();
        userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
        assertThat(userResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(userResponse.getUser()).isEqualTo(userDTO);

        userDTO.setId(MEMBER_ID);
        userDTO.setEmail("p3487260@gmail.com");
        userStr = objectMapper.writeValueAsString(userDTO);

        mvcResult = mockMvc.perform(
                post(Endpoints.CREATE_USER).with(oidcLogin().oidcUser(oidcUser)).contentType(MediaType.APPLICATION_JSON)
                        .content(userStr)).andExpect(status().isOk()).andReturn();
        userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
        assertThat(userResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(userResponse.getUser()).isEqualTo(userDTO);

        logTestCasePassed("Users for family", "Tests whether users is created");
    }

    @Test
    @Order(1)
    public void shouldCreateFamily() throws Exception {

        String description = "Testing family";
        FamilyDTO.Visibility visibility = FamilyDTO.Visibility.PRIVATE;
        String familyName = "Testing";

        FamilyCreationPayload familyDTO = new FamilyCreationPayload();
        familyDTO.setDescription(description);
        familyDTO.setVisibility(visibility);
        familyDTO.setName(familyName);
        familyDTO.setImage("/testing.png");

        MvcResult mvcResult = mockMvc.perform(post(Endpoints.CREATE_FAMILY).with(
                                oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family" +
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

        familyId = family.getId();

        logTestCasePassed("Create Family", "Tests family creation");
    }

    @Test
    @Order(2)
    public void shouldGetFamily() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get(UriComponentsBuilder.fromPath(Endpoints.GET_FAMILY).buildAndExpand(familyId).toUriString()).with(
                                oidcLogin().oidcUser(getOidcUser(USER_ID,
                                        List.of("SCOPE_ExpenseManager.Family.READ")))))
                .andExpect(status().isOk()).andReturn();
        FamilyResponse response =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FamilyResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        FamilyDTO familyDTO = response.getFamily();
        assertThat(familyDTO.getName()).isEqualTo("Testing");
        assertThat(familyDTO.getDescription()).isEqualTo("Testing family");
        assertThat(familyDTO.getVisibility()).isEqualTo(FamilyDTO.Visibility.PRIVATE);
        assertThat(familyDTO.getCreatedTime()).isNotNull();
        assertThat(familyDTO.getCreatedBy().getId()).isEqualTo(USER_ID);
    }

    @Test
    @Order(3)
    public void shouldGETUnknownFamilyResultInNotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get(UriComponentsBuilder.fromPath(Endpoints.GET_FAMILY).buildAndExpand("SOME_FAKE_FAMILY_ID")
                                .toUriString()).with(
                                oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family" +
                                        ".READ")))))
                .andExpect(status().isNotFound()).andReturn();
        Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

        logTestCasePassed("Get unknown family", "Get unknown family test passed!");
    }

    @Test
    @Order(4)
    public void shouldInviteMember() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                        post(UriComponentsBuilder.fromPath(Endpoints.INVITE_MEMBER).buildAndExpand(familyId, MEMBER_ID)
                                .toUriString()).param("role", FamilyMemberDTO.Role.MEMBER.name()).with(oidcLogin().oidcUser(
                                getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.Member" + ".INVITE")))))
                .andExpect(status().isOk()).andReturn();
        Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        mvcResult = mockMvc.perform(get(Endpoints.GET_ALL_INVITATIONS).with(
                        oidcLogin().oidcUser(getOidcUser(MEMBER_ID, List.of("SCOPE_ExpenseManager.Invitation.READ")))))
                .andExpect(status().isOk()).andReturn();

        InvitationsResponse invitationsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), InvitationsResponse.class);
        assertThat(invitationsResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(invitationsResponse.getInvitations().size()).isGreaterThan(0);

        InvitationDTO invitation = invitationsResponse.getInvitations().get(0);
        mvcResult = mockMvc.perform(
                        post(UriComponentsBuilder.fromPath(Endpoints.ACCEPT_INVITATION).buildAndExpand(invitation.getId())
                                .toUriString()).with(oidcLogin().oidcUser(
                                getOidcUser(MEMBER_ID, List.of("SCOPE_ExpenseManager.Invitation.ACCEPT")))))
                .andExpect(status().isOk()).andReturn();

        response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        logTestCasePassed("Family Member Invite", "Inviting member to family test passed!");
    }

    @Test
    @Order(5)
    public void shouldUpdateFamily() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get(UriComponentsBuilder.fromPath(Endpoints.GET_FAMILY).buildAndExpand(familyId).toUriString()).with(
                                oidcLogin().oidcUser(getOidcUser(USER_ID,
                                        List.of("SCOPE_ExpenseManager.Family.READ")))))
                .andExpect(status().isOk()).andReturn();
        FamilyResponse response =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FamilyResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        FamilyDTO familyDTO = response.getFamily();

        String updatedName = familyDTO.getName() + "_updated";
        String updatedDescription = familyDTO.getDescription() + "_updated";
        FamilyDTO.Visibility updatedVisibility = FamilyDTO.Visibility.PUBLIC;
        String updateImage = familyDTO.getImage() + "_updated";

        FamilyCreationPayload payload = new FamilyCreationPayload();
        payload.setName(updatedName);
        payload.setDescription(updatedDescription);
        payload.setImage(updateImage);
        payload.setVisibility(updatedVisibility);

        mvcResult = mockMvc.perform(
                        patch(UriComponentsBuilder.fromPath(Endpoints.UPDATE_FAMILY).buildAndExpand(familyId)
                                .toUriString()).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(payload)).with(oidcLogin().oidcUser(
                                        getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.UPDATE")))))
                .andExpect(status().isOk()).andReturn();

        response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FamilyResponse.class);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getFamily()).isNotNull();

        FamilyDTO updatedFamily = response.getFamily();

        assertThat(updatedFamily.getName()).isEqualTo(updatedName);
        assertThat(updatedFamily.getDescription()).isEqualTo(updatedDescription);
        assertThat(updatedFamily.getImage()).isEqualTo(updateImage);
        assertThat(updatedFamily.getVisibility()).isEqualTo(updatedVisibility);

        logTestCasePassed("Update Family", "Update family test case passed!");
    }

    @Test
    @Order(6)
    public void shouldUpdateFamilyByOtherUserFail() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get(UriComponentsBuilder.fromPath(Endpoints.GET_FAMILY).buildAndExpand(familyId).toUriString()).with(
                                oidcLogin().oidcUser(getOidcUser(MEMBER_ID, List.of("SCOPE_ExpenseManager.Family" +
                                        ".READ")))))
                .andExpect(status().isOk()).andReturn();
        FamilyResponse response =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FamilyResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        FamilyCreationPayload payload = getFamilyCreationPayload(response);

        mvcResult = mockMvc.perform(
                        patch(UriComponentsBuilder.fromPath(Endpoints.UPDATE_FAMILY).buildAndExpand(familyId)
                                .toUriString()).contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(payload)).with(oidcLogin().oidcUser(
                                        getOidcUser(MEMBER_ID, List.of("SCOPE_ExpenseManager.Family.UPDATE")))))
                .andExpect(status().isForbidden()).andReturn();

        Response errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);

        assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());

        logTestCasePassed("Update Family by non leader or maintainer",
                "Update family by non leader or maintainer test case passed!");
    }

    @Test
    @Order(7)
    public void shouldRemoveMemberFromFamily() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete(UriComponentsBuilder.fromPath(Endpoints.REMOVE_MEMBER_FROM_FAMILY)
                        .buildAndExpand(familyId, MEMBER_ID).toUriString()).with(
                        oidcLogin().oidcUser(getOidcUser(USER_ID,
                                List.of("SCOPE_ExpenseManager.Family.Member.REMOVE")))))
                .andExpect(status().isOk()).andReturn();
        Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        logTestCasePassed("Remove Family Member", "Removing member from family test passed!");
    }

    @Test
    @Order(8)
    public void shouldDeleteUnknownFamilyFail() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        delete(UriComponentsBuilder.fromPath(Endpoints.DELETE_FAMILY).buildAndExpand(
                                "SOME_FAKE_FAMILY_ID")
                                .toUriString()).with(
                                oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family" +
                                        ".DELETE")))))
                .andExpect(status().isBadRequest()).andReturn();
        Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        logTestCasePassed("Delete unknown family", "Delete unknown family test passed!");
    }

    @Test
    @Order(9)
    public void shouldDeleteFamily() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        delete(UriComponentsBuilder.fromPath(Endpoints.DELETE_FAMILY).buildAndExpand(familyId)
                                .toUriString()).with(
                                oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family" +
                                        ".DELETE")))))
                .andExpect(status().isOk()).andReturn();
        Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        logTestCasePassed("Delete family", "Delete family test passed!");
    }

    private FamilyCreationPayload getFamilyCreationPayload(FamilyResponse response) {
        FamilyDTO familyDTO = response.getFamily();

        String updatedName = familyDTO.getName();
        String updatedDescription = familyDTO.getDescription();
        FamilyDTO.Visibility updatedVisibility = FamilyDTO.Visibility.PUBLIC;
        String updateImage = familyDTO.getImage();

        FamilyCreationPayload payload = new FamilyCreationPayload();
        payload.setName(updatedName);
        payload.setDescription(updatedDescription);
        payload.setImage(updateImage);
        payload.setVisibility(updatedVisibility);
        return payload;
    }

    @Data
    private static class FamilyCreationPayload {
        private String name;
        private String description;
        private FamilyDTO.Visibility visibility;
        private String image;
    }
}
