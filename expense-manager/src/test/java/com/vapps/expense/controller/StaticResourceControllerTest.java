package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.dto.response.StaticResourceResponse;
import com.vapps.expense.common.dto.response.UserResponse;
import com.vapps.expense.common.util.Endpoints;
import com.vapps.expense.config.EnableMongoTestServer;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.vapps.expense.util.TestUtil.getOidcUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = { StaticResourceController.class })
@AutoConfigureMockMvc
@EnableMongoTestServer
@EnableMongoRepositories(basePackages = "com.vapps.expense.repository.mongo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StaticResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyControllerTest.class);
    private static String resourceId;
    private static String privateResourceId;
    private static String publicResourceId;

    @BeforeEach
    public void setup() throws Exception {
        createUser("user", "testuser");
        createUser("another", "testuseranother");
    }

    @Test
    @Order(1)
    @WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.StaticResource.CREATE" })
    void testAddPrivateResource() throws Exception {
        MockMultipartFile file = new MockMultipartFile("resource", "invoice.pdf", MediaType.APPLICATION_PDF_VALUE,
                "Test PDF content" .getBytes(StandardCharsets.UTF_8));

        MvcResult result = mockMvc.perform(multipart(Endpoints.STATIC_RESOURCE_API).file(file).param("private", "true")
                        .contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200)).andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.resourceId").exists()).andReturn();
        StaticResourceResponse response =
                objectMapper.readValue(result.getResponse().getContentAsString(), StaticResourceResponse.class);
        resourceId = response.getResourceId();
        privateResourceId = resourceId;
    }

    @Test
    @Order(2)
    @WithMockUser(username = "another", authorities = { "SCOPE_ExpenseManager.StaticResource.CREATE" })
    void testAddPublicResource() throws Exception {
        MockMultipartFile file = new MockMultipartFile("resource", "invoice.pdf", MediaType.APPLICATION_PDF_VALUE,
                "Test PDF content" .getBytes(StandardCharsets.UTF_8));

        MvcResult result = mockMvc.perform(
                        multipart(Endpoints.STATIC_RESOURCE_API).file(file).contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("success")).andExpect(jsonPath("$.resourceId").exists())
                .andReturn();
        StaticResourceResponse response =
                objectMapper.readValue(result.getResponse().getContentAsString(), StaticResourceResponse.class);
        publicResourceId = response.getResourceId();
    }

    @Test
    @Order(3)
    @WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.StaticResource.READ" })
    void testGetResource() throws Exception {
        mockMvc.perform(get(Endpoints.STATIC_RESOURCE_API + "/" + resourceId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(content().string("Test PDF content"));
    }

    @Test
    @Order(4)
    @WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.StaticResource.DELETE" })
    void testDeleteResource() throws Exception {
        mockMvc.perform(
                        delete(Endpoints.STATIC_RESOURCE_API + "/" + resourceId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Deleted resource!"));
    }

    @Test
    @Order(5)
    @WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.StaticResource.READ" })
    void testGetResource_NotFound() throws Exception {

        mockMvc.perform(get(Endpoints.STATIC_RESOURCE_API + "/resourceId")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("The requested resource not found"));

    }

    @Test
    @Order(6)
    @WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.StaticResource.CREATE" })
    void testAddResource_UnsupportedMediaType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("resource", "file.txt", "text/dummy", "some data" .getBytes());

        mockMvc.perform(multipart(Endpoints.STATIC_RESOURCE_API).file(file).contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnsupportedMediaType()).andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.error").value("Unsupported Resource type text/dummy"));
    }

    @Test
    @Order(7)
    @WithMockUser(username = "another", authorities = { "SCOPE_ExpenseManager.StaticResource.READ" })
    void testGetResource_Private() throws Exception {
        mockMvc.perform(
                        get(Endpoints.STATIC_RESOURCE_API + "/" + privateResourceId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("The requested resource not found"));

    }

    @Test
    @Order(8)
    @WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.StaticResource.READ" })
    void testGetPublicResource() throws Exception {
        mockMvc.perform(
                        get(Endpoints.STATIC_RESOURCE_API + "/" + publicResourceId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(content().string("Test PDF content"));
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
}
