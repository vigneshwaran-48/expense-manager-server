package com.vapps.expense.controller;

import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.dto.response.FamilyResponse;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.util.Endpoints;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(Endpoints.FAMILY_API)
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

    @PostMapping(Endpoints.INVITE_MEMBER_PATH)
    public ResponseEntity<Response> inviteMember(@PathVariable String familyId, @PathVariable String memberId,
            @RequestParam FamilyMemberDTO.Role role, Principal principal, HttpServletRequest request)
            throws AppException {
        String userId = principal.getName();

        familyService.inviteMember(userId, familyId, memberId, role);

        return ResponseEntity.ok(
                new Response(HttpStatus.OK.value(), "Invited member!", LocalDateTime.now(), request.getServletPath()));
    }

    @DeleteMapping(Endpoints.REMOVE_MEMBER_FROM_FAMILY_PATH)
    public ResponseEntity<Response> removeMember(@PathVariable String familyId, @PathVariable String memberId,
            Principal principal, HttpServletRequest request) throws AppException {
        String userId = principal.getName();

        familyService.removeMember(userId, familyId, memberId);
        return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Removed member from family!", LocalDateTime.now(),
                request.getServletPath()));
    }

    @GetMapping(Endpoints.GET_FAMILY_PATH)
    public ResponseEntity<FamilyResponse> getFamily(@PathVariable String familyId, Principal principal,
            HttpServletRequest request) throws AppException {

        String userId = principal.getName();
        Optional<FamilyDTO> familyDTO = familyService.getFamilyById(userId, familyId);
        if (familyDTO.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND.value(), "Family not found!");
        }
        return ResponseEntity.ok(
                new FamilyResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
                        familyDTO.get()));
    }

    @PatchMapping(Endpoints.UPDATE_FAMILY_PATH)
    public ResponseEntity<FamilyResponse> updateFamily(@PathVariable String familyId, @RequestBody FamilyDTO familyDTO,
            Principal principal, HttpServletRequest request) throws AppException {

        String userId = principal.getName();
        FamilyDTO updatedFamily = familyService.updateFamily(userId, familyId, familyDTO);
        return ResponseEntity.ok(
                new FamilyResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
                        updatedFamily));
    }

    @DeleteMapping(Endpoints.DELETE_FAMILY_PATH)
    public ResponseEntity<Response> deleteFamily(@PathVariable String familyId, Principal principal,
            HttpServletRequest request) throws AppException {

        String userId = principal.getName();
        familyService.deleteFamilyById(userId, familyId);
        return ResponseEntity.ok(
                new Response(HttpStatus.OK.value(), "Deleted family!", LocalDateTime.now(), request.getServletPath()));
    }
}
