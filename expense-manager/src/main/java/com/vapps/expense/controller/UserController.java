package com.vapps.expense.controller;

import com.vapps.expense.common.dto.ExpenseStatsDTO;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.dto.response.ExpenseStatsResponse;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.dto.response.UserResponse;
import com.vapps.expense.common.dto.response.UsersResponse;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.ExpenseStatsService;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.service.UserService;
import com.vapps.expense.common.util.Endpoints;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;

@RestController
@RequestMapping(Endpoints.USER_API)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseStatsService statsService;

    @Autowired
    private FamilyService familyService;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserDTO user, HttpServletRequest request)
            throws AppException {
        UserDTO userDTO = userService.addUser(user);
        return ResponseEntity.ok(
                new UserResponse(HttpStatus.OK.value(), "Created User", LocalDateTime.now(), request.getServletPath(),
                        userDTO));
    }

    @GetMapping(Endpoints.GET_USER_PATH)
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId, HttpServletRequest request)
            throws AppException {
        UserDTO userDTO = userService.getUser(userId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST.value(), "User " + "not found"));
        return ResponseEntity.ok(
                new UserResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
                        userDTO));
    }

    @PatchMapping(Endpoints.UPDATE_USER_PATH)
    public ResponseEntity<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserDTO user,
            HttpServletRequest request, Principal principal) throws AppException {

        String currentUserId = principal.getName();
        UserDTO updatedUser = userService.updateUser(currentUserId, userId, user);
        return ResponseEntity.ok(
                new UserResponse(HttpStatus.OK.value(), "Updated User", LocalDateTime.now(), request.getServletPath(),
                        updatedUser));
    }

    @GetMapping(Endpoints.GET_PROFILE_PATH)
    public ResponseEntity<UserResponse> getProfile(HttpServletRequest request, Principal principal)
            throws AppException {

        String userId = principal.getName();
        UserDTO userDTO = userService.getUser(userId)
                .orElseThrow(() -> new AppException(401, "Current user not " + userId + " exists!"));
        return ResponseEntity.ok(
                new UserResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
                        userDTO));
    }

    @GetMapping
    public ResponseEntity<UsersResponse> getAllUsers(@RequestParam(defaultValue = "true") boolean includeFamilyMember,
            @RequestParam String query, HttpServletRequest request, Principal principal) throws AppException {
        String userId = principal.getName();
        List<UserDTO> users = null;
        if (includeFamilyMember) {
            users = userService.findAllUser();
        } else {
            users = familyService.getNonFamilyAndNonInvitedUsers(userId);
        }
        if (query != null) {
            users = users.stream().filter(user -> user.getId().equals(query) || user.getName().toLowerCase()
                    .contains(query.toLowerCase()) || user.getEmail().contains(query)).toList();
        }
        userService.findAllUser();
        return ResponseEntity.ok(
                new UsersResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
                        users));
    }

    @PostMapping(Endpoints.LOGOUT_PATH)
    public ResponseEntity<Response> logout(Principal principal, HttpServletRequest request) throws AppException {
        RestTemplate restTemplate = new RestTemplate();
        String tokenHeaderValue = request.getHeader("Authorization");
        if (tokenHeaderValue == null || tokenHeaderValue.isBlank()) {
            throw new AppException(401, "Not authenticated!");
        }
        String[] tokenHeaderValueSplit = tokenHeaderValue.split("Bearer ");
        if (tokenHeaderValueSplit.length < 2) {
            throw new AppException(401, "Invalid access token");
        }

        RequestEntity entity =
                RequestEntity.post(issuer + "/logout").header("Authorization", "Bearer " + tokenHeaderValueSplit[1])
                        .build();
        ResponseEntity<String> response = restTemplate.exchange(entity, String.class);
        if (response.getStatusCode() == HttpStatus.FOUND) {
            LOGGER.info(response.getHeaders().get("Location").toString());
        } else if (response.getStatusCode() != HttpStatus.OK) {
            throw new AppException(response.getStatusCode().value(), "Error from Authorization server!");
        }
        return ResponseEntity.ok(
                new Response(HttpStatus.OK.value(), "Logged out!", LocalDateTime.now(), request.getServletPath()));
    }

}
