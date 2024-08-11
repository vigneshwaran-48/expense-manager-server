package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.dto.response.FamilyResponse;
import com.vapps.expense.common.dto.response.UserResponse;
import com.vapps.expense.common.util.Endpoints;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.vapps.expense.util.TestUtil.getOidcUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
