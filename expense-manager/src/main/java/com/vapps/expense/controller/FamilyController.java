package com.vapps.expense.controller;

import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.dto.response.FamilyResponse;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.FamilyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{familyId}/member/{memberId}/invite")
    public ResponseEntity<Response> inviteMember(@PathVariable String familyId, @PathVariable String memberId,
            @RequestParam FamilyMemberDTO.Role role, Principal principal, HttpServletRequest request)
            throws AppException {
        String userId = principal.getName();

        familyService.inviteMember(userId, familyId, memberId, role);

        return ResponseEntity.ok(
                new Response(HttpStatus.OK.value(), "Invited member!", LocalDateTime.now(), request.getServletPath()));
    }

    @DeleteMapping("/{familyId}/member/{memberId}")
    public ResponseEntity<Response> removeMember(@PathVariable String familyId, @PathVariable String memberId,
            Principal principal, HttpServletRequest request) throws AppException {
        String userId = principal.getName();

        familyService.removeMember(userId, familyId, memberId);
        return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Removed member from family!", LocalDateTime.now(),
                request.getServletPath()));
    }
}
