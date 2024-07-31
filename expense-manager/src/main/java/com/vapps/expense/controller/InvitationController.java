package com.vapps.expense.controller;

import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.common.dto.response.InvitationsResponse;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.InvitationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/invitation")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @PostMapping("/{id}/accept")
    public ResponseEntity<Response> acceptInvitation(@PathVariable String id, Principal principal,
            HttpServletRequest request) throws AppException {
        String userId = principal.getName();
        invitationService.acceptInvitation(userId, id);

        return ResponseEntity.ok(
                new Response(HttpStatus.OK.value(), "Invite accepted!", LocalDateTime.now(), request.getServletPath()));
    }

    @GetMapping
    public ResponseEntity<InvitationsResponse> getAllInvitations(Principal principal, HttpServletRequest request)
            throws AppException {
        String userId = principal.getName();
        List<InvitationDTO> invitations = invitationService.getAllInvitations(userId);
        return ResponseEntity.ok(
                new InvitationsResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
                        invitations));
    }
}
