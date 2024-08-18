package com.vapps.expense.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.response.StaticResourceResponse;
import com.vapps.expense.common.util.Endpoints;
import com.vapps.expense.config.EnableMongoTestServer;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static com.vapps.expense.controller.ControllerTestUtil.createUser;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = { StaticResourceController.class })
@AutoConfigureMockMvc
@EnableMongoTestServer
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
        createUser(mockMvc, objectMapper, "user", "testuser");
        createUser(mockMvc, objectMapper, "another", "testuseranother");
    }

    @Test
    @Order(1)
    @WithMockUser(username = "user", authorities = { "SCOPE_ExpenseManager.StaticResource.CREATE" })
    void testAddPrivateResource() throws Exception {
        MockMultipartFile file = new MockMultipartFile("resource", "invoice.pdf", MediaType.APPLICATION_PDF_VALUE,
                "Test PDF content".getBytes(StandardCharsets.UTF_8));

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
                "Test PDF content".getBytes(StandardCharsets.UTF_8));

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
        MockMultipartFile file = new MockMultipartFile("resource", "file.txt", "text/dummy", "some data".getBytes());

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

}
