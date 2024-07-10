package com.vapps.expense.controller;

import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.dto.response.UserResponse;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Response> createUser(@RequestBody UserDTO user, HttpServletRequest request)
            throws AppException {
        userService.addUser(user);
        return ResponseEntity.ok(
                new Response(HttpStatus.OK.value(), "Created User", LocalDateTime.now(), request.getServletPath()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId, HttpServletRequest request)
            throws AppException {
        UserDTO userDTO = userService.getUser(userId)
                .orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST.value(), "User " + "not found"));
        return ResponseEntity.ok(
                new UserResponse(HttpStatus.OK.value(), "Created User", LocalDateTime.now(), request.getServletPath(),
                        userDTO));
    }

}
