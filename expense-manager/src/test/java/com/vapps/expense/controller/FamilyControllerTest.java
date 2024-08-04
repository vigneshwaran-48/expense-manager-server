package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.dto.response.*;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
        createUser(USER_ID, "Vigneshwaran");
        createUser(MEMBER_ID, "Member");
        createUser("smith_id", "smith");
        createUser("john_id", "john");
        createUser("martinez_id", "martinez");
        createUser("brown_id", "brown");
        createUser("williams_id", "williams");
        createUser("garcia_id", "garcia");
        createUser("rodriguez_id", "rodriguez");
        createUser("lee_id", "lee");
        createUser("young_id", "young");
        createUser("nguyen_id", "nguyen");
        createUser("patel_id", "patel");
        createUser("wilson_id", "wilson");
        createUser("walker_id", "walker");
        createUser("harris_id", "harris");
        createUser("hernandez_id", "hernandez");
        createUser("white_id", "white");
        createUser("thompson_id", "thompson");
        createUser("ramirez_id", "ramirez");
        createUser("jones_id", "jones");
        createUser("greene_id", "greene");

        logTestCasePassed("Users for family", "Tests whether users is created");
    }

    @Test
    @Order(1)
    public void shouldCreateFamily() throws Exception {
        familyId = createFamily(USER_ID, "Testing", FamilyDTO.Visibility.PRIVATE);
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
    public void shouldGetUserFamily() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        get(UriComponentsBuilder.fromPath(Endpoints.GET_USER_FAMILY).toUriString()).with(
                                oidcLogin().oidcUser(getOidcUser(USER_ID,
                                        List.of("SCOPE_ExpenseManager.Family.READ")))))
                .andExpect(status().isOk()).andReturn();
        FamilyResponse response =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FamilyResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getFamily()).isNotNull();

        logTestCasePassed("Get user's family", "Getting user's family test passed!");
    }

    @Test
    @Order(8)
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
    @Order(9)
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
    @Order(10)
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

    @Test
    @Order(11)
    public void shouldSearchFamily() throws Exception {
        createFamily("smith_id", "The Smiths", FamilyDTO.Visibility.PUBLIC);
        createFamily("john_id", "Johnson Clan", FamilyDTO.Visibility.PUBLIC);
        createFamily("martinez_id", "The Martinez Family", FamilyDTO.Visibility.PUBLIC);
        createFamily("brown_id", "Brown Household", FamilyDTO.Visibility.PUBLIC);
        createFamily("williams_id", "Williams Crew", FamilyDTO.Visibility.PUBLIC);
        createFamily("garcia_id", "Garcia Tribe", FamilyDTO.Visibility.PUBLIC);
        createFamily("rodriguez_id", "Rodriguez Kin", FamilyDTO.Visibility.PUBLIC);
        createFamily("lee_id", "Lee Dynasty", FamilyDTO.Visibility.PUBLIC);
        createFamily("young_id", "The Youngs", FamilyDTO.Visibility.PUBLIC);
        createFamily("nguyen_id", "Nguyen Household", FamilyDTO.Visibility.PUBLIC);
        createFamily("patel_id", "Patel Family", FamilyDTO.Visibility.PUBLIC);
        createFamily("wilson_id", "The Wilsons", FamilyDTO.Visibility.PUBLIC);
        createFamily("walker_id", "The Walkers", FamilyDTO.Visibility.PUBLIC);
        createFamily("harris_id", "Harris Household", FamilyDTO.Visibility.PUBLIC);
        createFamily("hernandez_id", "The Hernandez Family", FamilyDTO.Visibility.PUBLIC);
        createFamily("white_id", "White Clan", FamilyDTO.Visibility.PUBLIC);
        createFamily("thompson_id", "The Thompson Tribe", FamilyDTO.Visibility.PUBLIC);
        createFamily("ramirez_id", "The Ramirezes", FamilyDTO.Visibility.PUBLIC);
        createFamily("jones_id", "Jones Unit", FamilyDTO.Visibility.PUBLIC);
        createFamily("greene_id", "The Greenes", FamilyDTO.Visibility.PUBLIC);

        // Searching for more results
        MvcResult result = mockMvc.perform(get(Endpoints.SEARCH_FAMILY).param("query", "e").param("page", "1")
                        .with(oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.READ")))))
                .andExpect(status().isOk()).andReturn();

        SearchResponse<SearchDTO> searchResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), SearchResponse.class);

        assertThat(searchResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(searchResponse.getResult().getResults().size()).isEqualTo(10);
        assertThat(searchResponse.getResult().getCurrentPage()).isEqualTo(1);
        assertThat(searchResponse.getResult().getNextPage()).isEqualTo(2);

        // Searching for next results
        result = mockMvc.perform(get(Endpoints.SEARCH_FAMILY).param("query", "e").param("page", "2")
                        .with(oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.READ")))))
                .andExpect(status().isOk()).andReturn();

        searchResponse = objectMapper.readValue(result.getResponse().getContentAsString(), SearchResponse.class);

        assertThat(searchResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(searchResponse.getResult().getCurrentPage()).isEqualTo(2);

        logTestCasePassed("Search family", "Search family test passed!");
    }

    private String createFamily(String userId, String familyName, FamilyDTO.Visibility visibility) throws Exception {
        String description = "Testing family";

        FamilyCreationPayload familyDTO = new FamilyCreationPayload();
        familyDTO.setDescription(description);
        familyDTO.setVisibility(visibility);
        familyDTO.setName(familyName);
        familyDTO.setImage("/testing.png");

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

        return family.getId();
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

    private String createUser(String userId, String userName) throws Exception {

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

    @Data
    private static class FamilyCreationPayload {
        private String name;
        private String description;
        private FamilyDTO.Visibility visibility;
        private String image;
    }
}
