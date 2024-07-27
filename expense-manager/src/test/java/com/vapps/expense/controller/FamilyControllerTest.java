package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.dto.response.FamilyResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FamilyController.class)
@AutoConfigureMockMvc
@EnableMongoTestServer
@EnableMongoRepositories(basePackages = "com.vapps.expense.repository.mongo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FamilyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyControllerTest.class);
    private static final String USER_ID = "testing_user_id";

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
        userDTO.setEmail("vignesh@test.com");
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
        logTestCasePassed("Create user", "Tests whether user is created");
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

        logTestCasePassed("Create Family", "Tests family creation");
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
