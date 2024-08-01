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

        MvcResult getUserResult =
                mockMvc.perform(get("/api/user/" + USER_ID).with(oidcLogin().oidcUser(oidcUser))).andReturn();
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
                post("/api/user").with(oidcLogin().oidcUser(oidcUser)).contentType(MediaType.APPLICATION_JSON)
                        .content(userStr)).andExpect(status().isOk()).andReturn();
        userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
        assertThat(userResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(userResponse.getUser()).isEqualTo(userDTO);

        userDTO.setId(MEMBER_ID);
        userDTO.setEmail("p3487260@gmail.com");
        userStr = objectMapper.writeValueAsString(userDTO);

        mvcResult = mockMvc.perform(
                post("/api/user").with(oidcLogin().oidcUser(oidcUser)).contentType(MediaType.APPLICATION_JSON)
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

        MvcResult mvcResult = mockMvc.perform(post("/api/family").with(
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
        MvcResult mvcResult = mockMvc.perform(get("/api/family/" + familyId).with(
                        oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.READ")))))
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
    public void shouldInviteMember() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/family/" + familyId + "/member/" + MEMBER_ID + "/invite").param("role",
                                FamilyMemberDTO.Role.MEMBER.name()).with(oidcLogin().oidcUser(
                                getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.Member.INVITE")))))
                .andExpect(status().isOk()).andReturn();
        Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        mvcResult = mockMvc.perform(get("/api/invitation").with(
                        oidcLogin().oidcUser(getOidcUser(MEMBER_ID, List.of("SCOPE_ExpenseManager.Invitation.READ")))))
                .andExpect(status().isOk()).andReturn();

        InvitationsResponse invitationsResponse =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), InvitationsResponse.class);
        assertThat(invitationsResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(invitationsResponse.getInvitations().size()).isGreaterThan(0);

        InvitationDTO invitation = invitationsResponse.getInvitations().get(0);
        mvcResult = mockMvc.perform(post("/api/invitation/" + invitation.getId() + "/accept").with(
                        oidcLogin().oidcUser(getOidcUser(MEMBER_ID,
                                List.of("SCOPE_ExpenseManager.Invitation.ACCEPT")))))
                .andExpect(status().isOk()).andReturn();

        response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        logTestCasePassed("Family Member Invite", "Inviting member to family test passed!");
    }

    @Test
    @Order(4)
    public void shouldRemoveMemberFromFamily() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/family/" + familyId + "/member/" + MEMBER_ID).with(
                        oidcLogin().oidcUser(getOidcUser(USER_ID,
                                List.of("SCOPE_ExpenseManager.Family.Member.REMOVE")))))
                .andExpect(status().isOk()).andReturn();
        Response response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        logTestCasePassed("Remove Family Member", "Removing member from family test passed!");
    }

    @Test
    @Order(5)
    public void shouldUpdateFamily() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/family/" + familyId).with(
                        oidcLogin().oidcUser(getOidcUser(USER_ID, List.of("SCOPE_ExpenseManager.Family.READ")))))
                .andExpect(status().isOk()).andReturn();
        FamilyResponse response =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FamilyResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        FamilyDTO familyDTO = response.getFamily();

        String updatedName = familyDTO.getName() + "_updated";
        String updatedDescription = familyDTO.getDescription() + "_updated";
        FamilyDTO.Visibility updatedVisibility = FamilyDTO.Visibility.PUBLIC;
        String updateImage = familyDTO.getImage() + "_updated";

        familyDTO.setName(updatedName);
        familyDTO.setDescription(updatedDescription);
        familyDTO.setImage(updateImage);
        // TODO Need to complete this!
    }

    @Data
    private class UserWithIdAndEmail {
        private String id;
        private String email;
    }

    @Data
    private class FamilyCreationPayload {
        private String name;
        private String description;
        private FamilyDTO.Visibility visibility;
    }
}
