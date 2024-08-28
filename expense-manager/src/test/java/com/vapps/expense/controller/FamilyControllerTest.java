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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.vapps.expense.controller.ControllerTestUtil.*;
import static com.vapps.expense.util.TestUtil.getOidcUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {FamilyController.class, InvitationController.class})
@AutoConfigureMockMvc
@EnableMongoTestServer
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FamilyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String familyId;

    private static String requestId;

    private static String invitationId;

    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyControllerTest.class);
    private static final String USER_ID = "testing_user_id";
    private static final String MEMBER_ID = "testing_member_id";

    @BeforeEach
    public void setup() throws Exception {
        createUser(mockMvc, objectMapper, USER_ID, "Vigneshwaran");
        createUser(mockMvc, objectMapper, MEMBER_ID, "Member");
        createUser(mockMvc, objectMapper, "smith_id", "smith");
        createUser(mockMvc, objectMapper, "john_id", "john");
        createUser(mockMvc, objectMapper, "martinez_id", "martinez");
        createUser(mockMvc, objectMapper, "brown_id", "brown");
        createUser(mockMvc, objectMapper, "williams_id", "williams");
        createUser(mockMvc, objectMapper, "garcia_id", "garcia");
        createUser(mockMvc, objectMapper, "rodriguez_id", "rodriguez");
        createUser(mockMvc, objectMapper, "lee_id", "lee");
        createUser(mockMvc, objectMapper, "young_id", "young");
        createUser(mockMvc, objectMapper, "nguyen_id", "nguyen");
        createUser(mockMvc, objectMapper, "patel_id", "patel");
        createUser(mockMvc, objectMapper, "wilson_id", "wilson");
        createUser(mockMvc, objectMapper, "walker_id", "walker");
        createUser(mockMvc, objectMapper, "harris_id", "harris");
        createUser(mockMvc, objectMapper, "hernandez_id", "hernandez");
        createUser(mockMvc, objectMapper, "white_id", "white");
        createUser(mockMvc, objectMapper, "thompson_id", "thompson");
        createUser(mockMvc, objectMapper, "ramirez_id", "ramirez");
        createUser(mockMvc, objectMapper, "jones_id", "jones");
        createUser(mockMvc, objectMapper, "greene_id", "greene");

        // logTestCasePassed("Users for family", "Tests whether users is created");
    }

    @Test
    @Order(1)
    public void shouldCreateFamily() throws Exception {
        familyId = createFamily(mockMvc, objectMapper, USER_ID, "Testing", FamilyDTO.Visibility.PRIVATE);
        // logTestCasePassed("Create Family", "Tests family creation");
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

        // logTestCasePassed("Get unknown family", "Get unknown family test passed!");
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

        mvcResult = mockMvc.perform(get(UriComponentsBuilder.fromPath(Endpoints.GET_FAMILY_INVITATIONS)
                        .buildAndExpand(familyId).toUriString()).with(
                        oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.READ")))))
                .andExpect(status().isOk()).andReturn();

        InvitationsResponse invitationsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), InvitationsResponse.class);
        assertThat(invitationsResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(invitationsResponse.getInvitations().size()).isGreaterThan(0);

        mvcResult = mockMvc.perform(get(Endpoints.GET_ALL_INVITATIONS).with(
                        oidcLogin().oidcUser(getOidcUser(MEMBER_ID, List.of("SCOPE_ExpenseManager.Invitation.READ")))))
                .andExpect(status().isOk()).andReturn();

        invitationsResponse =
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

        // logTestCasePassed("Family Member Invite", "Inviting member to family test passed!");
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
        FamilyDTO.JoinType updatedJoinTye = FamilyDTO.JoinType.INVITE_ONLY;

        FamilyCreationPayload payload = new FamilyCreationPayload();
        payload.setName(updatedName);
        payload.setDescription(updatedDescription);
        payload.setImage(updateImage);
        payload.setVisibility(updatedVisibility);
        payload.setJoinType(updatedJoinTye);

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
        assertThat(updatedFamily.getJoinType()).isEqualTo(updatedJoinTye);

        // logTestCasePassed("Update Family", "Update family test case passed!");
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

        // logTestCasePassed("Update Family by non leader or maintainer",
        //                "Update family by non leader or maintainer test case passed!");
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

        // logTestCasePassed("Get user's family", "Getting user's family test passed!");
    }

    @Test
    @Order(8)
    public void shouldGetMembersOfFamily() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                        get(UriComponentsBuilder.fromPath(Endpoints.GET_FAMILY_MEMBERS).buildAndExpand(familyId)
                                .toUriString()).with(oidcLogin().oidcUser(
                                getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.Member" + ".READ")))))
                .andExpect(status().isOk()).andReturn();
        FamilyMembersResponse response =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FamilyMembersResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMembers().size()).isGreaterThan(0);
    }

    @Test
    @Order(9)
    public void shouldGetMemberOfFamily() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                        get(UriComponentsBuilder.fromPath(Endpoints.GET_FAMILY_MEMBER).buildAndExpand(familyId,
                                        MEMBER_ID)
                                .toUriString()).with(oidcLogin().oidcUser(
                                getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.Member" + ".READ")))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.member").exists()).andReturn();
        FamilyMemberResponse response =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FamilyMemberResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getMember().getMember().getId()).isEqualTo(MEMBER_ID);
        assertThat(response.getMember().getFamily().getId()).isEqualTo(familyId);
    }

    @Test
    @Order(10)
    public void shouldUpdateMemberRoleInFamily() throws Exception {

        mockMvc.perform(post(UriComponentsBuilder.fromPath(Endpoints.UPDATE_FAMILY_MEMBER_ROLE)
                        .buildAndExpand(familyId, MEMBER_ID).toUriString()).param("role",
                        FamilyMemberDTO.Role.MAINTAINER.name()).with(oidcLogin().oidcUser(
                        getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.Member" + ".UPDATE")))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));

    }

    @Test
    @Order(11)
    public void shouldRemoveMemberFromFamily() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete(UriComponentsBuilder.fromPath(Endpoints.REMOVE_MEMBER_FROM_FAMILY)
                        .buildAndExpand(familyId, MEMBER_ID).toUriString()).with(
                        oidcLogin().oidcUser(getOidcUser(USER_ID,
                                List.of("SCOPE_ExpenseManager.Family.Member.REMOVE")))))
                .andExpect(status().isOk()).andReturn();
        Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // logTestCasePassed("Remove Family Member", "Removing member from family test passed!");
    }

    @Test
    @Order(12)
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

        // logTestCasePassed("Delete unknown family", "Delete unknown family test passed!");
    }

    @Test
    @Order(13)
    public void shouldDeleteFamily() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        delete(UriComponentsBuilder.fromPath(Endpoints.DELETE_FAMILY).buildAndExpand(familyId)
                                .toUriString()).with(
                                oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family" +
                                        ".DELETE")))))
                .andExpect(status().isOk()).andReturn();
        Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        // logTestCasePassed("Delete family", "Delete family test passed!");
    }

    @Test
    @Order(14)
    public void shouldRejectJoinRequest() throws Exception {
        familyId = createFamily(mockMvc, objectMapper, USER_ID, "The Smiths Test", FamilyDTO.Visibility.PUBLIC);
        MvcResult mvcResult = mockMvc.perform(post(UriComponentsBuilder.fromPath(Endpoints.FAMILY_JOIN_REQUEST)
                        .buildAndExpand(familyId).toUriString()).with(oidcLogin().oidcUser(getOidcUser(MEMBER_ID,
                        List.of("SCOPE_ExpenseManager.Family.Request.CREATE"))))).andExpect(status().isOk())
                .andReturn();
        JoinRequestResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JoinRequestResponse.class);
        assertThat(response.getRequest().getId()).isNotNull();
        assertThat(response.getRequest().getFamily().getId()).isEqualTo(familyId);
        assertThat(response.getRequest().getRequestUser().getId()).isEqualTo(MEMBER_ID);

        mvcResult = mockMvc.perform(post(UriComponentsBuilder.fromPath(Endpoints.FAMILY_REJECT_JOIN_REQUEST)
                        .buildAndExpand(familyId, response.getRequest().getId()).toUriString()).with(oidcLogin().oidcUser(getOidcUser(USER_ID,
                        List.of("SCOPE_ExpenseManager.Family.Request.REJECT"))))).andExpect(status().isOk())
                .andReturn();
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
    }

    @Test
    @Order(15)
    public void shouldMakeJoinRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(UriComponentsBuilder.fromPath(Endpoints.FAMILY_JOIN_REQUEST)
                        .buildAndExpand(familyId).toUriString()).with(oidcLogin().oidcUser(getOidcUser(MEMBER_ID,
                        List.of("SCOPE_ExpenseManager.Family.Request.CREATE"))))).andExpect(status().isOk())
                .andExpect(jsonPath("$.request").exists()).andReturn();
        JoinRequestResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JoinRequestResponse.class);
        assertThat(response.getRequest().getId()).isNotNull();
        assertThat(response.getRequest().getFamily().getId()).isEqualTo(familyId);
        assertThat(response.getRequest().getRequestUser().getId()).isEqualTo(MEMBER_ID);

        requestId = response.getRequest().getId();
    }

    @Test
    @Order(16)
    public void shouldListJoinRequests() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(UriComponentsBuilder.fromPath(Endpoints.FAMILY_LIST_JOIN_REQUEST)
                        .buildAndExpand(familyId).toUriString()).with(oidcLogin().oidcUser(getOidcUser(USER_ID,
                        List.of("SCOPE_ExpenseManager.Family.Request.READ"))))).andExpect(status().isOk())
                .andExpect(jsonPath("$.requests").exists())
                .andReturn();
        JoinRequestsResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JoinRequestsResponse.class);
        assertThat(response.getRequests().get(0).getRequestUser().getId()).isEqualTo(MEMBER_ID);
    }

    @Test
    @Order(17)
    public void shouldAcceptJoinRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(UriComponentsBuilder.fromPath(Endpoints.FAMILY_ACCEPT_JOIN_REQUEST)
                        .buildAndExpand(familyId, requestId).toUriString()).with(oidcLogin().oidcUser(getOidcUser(USER_ID,
                        List.of("SCOPE_ExpenseManager.Family.Request.ACCEPT"))))).andExpect(status().isOk())
                .andReturn();
        objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
    }

    @Test
    @Order(18)
    public void shouldSearchFamily() throws Exception {
        createFamily(mockMvc, objectMapper, "smith_id", "The Smiths", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "john_id", "Johnson Clan", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "martinez_id", "The Martinez Family", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "brown_id", "Brown Household", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "williams_id", "Williams Crew", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "garcia_id", "Garcia Tribe", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "rodriguez_id", "Rodriguez Kin", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "lee_id", "Lee Dynasty", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "young_id", "The Youngs", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "nguyen_id", "Nguyen Household", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "patel_id", "Patel Family", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "wilson_id", "The Wilsons", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "walker_id", "The Walkers", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "harris_id", "Harris Household", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "hernandez_id", "The Hernandez Family", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "white_id", "White Clan", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "thompson_id", "The Thompson Tribe", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "ramirez_id", "The Ramirezes", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "jones_id", "Jones Unit", FamilyDTO.Visibility.PUBLIC);
        createFamily(mockMvc, objectMapper, "greene_id", "The Greenes", FamilyDTO.Visibility.PUBLIC);

        // Searching for more results
        MvcResult result = mockMvc.perform(get(Endpoints.SEARCH_FAMILY).param("query", "e").param("page", "1")
                        .with(oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.READ")))))
                .andExpect(status().isOk()).andReturn();

        SearchResponse<SearchDTO> searchResponse =
                objectMapper.readValue(result.getResponse().getContentAsString(), SearchResponse.class);

        LOGGER.info("Search Response => {}", searchResponse.toString());
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

        // logTestCasePassed("Search family", "Search family test passed!");
    }

    @Test
    @Order(19)
    public void shouldResendInvite() throws Exception {
        mockMvc.perform(delete(UriComponentsBuilder.fromPath(Endpoints.REMOVE_MEMBER_FROM_FAMILY)
                        .buildAndExpand(familyId, MEMBER_ID).toUriString()).with(
                        oidcLogin().oidcUser(getOidcUser(USER_ID,
                                List.of("SCOPE_ExpenseManager.Family.Member.REMOVE")))))
                .andExpect(status().isOk()).andReturn();

        MvcResult mvcResult = mockMvc.perform(
                        post(UriComponentsBuilder.fromPath(Endpoints.INVITE_MEMBER).buildAndExpand(familyId, MEMBER_ID)
                                .toUriString()).param("role", FamilyMemberDTO.Role.MEMBER.name()).with(oidcLogin().oidcUser(
                                getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.Member" + ".INVITE")))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.invitation").exists()).andReturn();
        InvitationResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), InvitationResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        InvitationDTO invitation = response.getInvitation();
        invitationId = invitation.getId();

        mockMvc.perform(post(UriComponentsBuilder.fromPath(Endpoints.RESEND_INVITATION).buildAndExpand(invitation.getId()).toUriString())
                        .with(oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.Member.INVITE")))))
                .andExpect(status().isOk());
    }

    @Test
    @Order(20)
    public void shouldRevokeInvite() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(UriComponentsBuilder.fromPath(Endpoints.REVOKE_INVITATION).buildAndExpand(invitationId).toUriString())
                        .with(oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.Member.INVITE")))))
                .andExpect(status().isOk()).andReturn();
        Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
    }

    @Data
    public static class FamilyCreationPayload {
        private String name;
        private String description;
        private FamilyDTO.Visibility visibility;
        private String image;
        private FamilyDTO.JoinType joinType;
    }
}
