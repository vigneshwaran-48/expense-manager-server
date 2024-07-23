package com.vapps.expense.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.dto.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.*;

//@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@DataMongoTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
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

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/user").contentType(MediaType.APPLICATION_JSON).content(userStr))
                .andExpect(status().isOk()).andReturn();
        UserResponse userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                UserResponse.class);
        assertThat(userResponse.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY.value());
        assertThat(userResponse.getUser()).isEqualTo(userDTO);
    }
}
