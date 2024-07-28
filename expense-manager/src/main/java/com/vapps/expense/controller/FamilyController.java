package com.vapps.expense.controller;

import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.response.FamilyResponse;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.FamilyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/family")
public class FamilyController {

    @Autowired
    private FamilyService familyService;

    @PostMapping
    public ResponseEntity<FamilyResponse> createFamily(@RequestBody FamilyDTO family, Principal principal,
            HttpServletRequest request) throws AppException {

        String userId = principal.getName();
        FamilyDTO createdFamily = familyService.createFamily(userId, family);

        return ResponseEntity.ok(new FamilyResponse(HttpStatus.OK.value(), "Created Family!", LocalDateTime.now(),
                request.getServletPath(), createdFamily));
    }
}
