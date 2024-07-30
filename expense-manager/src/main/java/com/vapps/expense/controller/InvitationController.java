package com.vapps.expense.controller;

import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/invitation")
public class InvitationController {

    @Autowired
    private InvitationService invitationService;

    @PostMapping("/{id}/accept")
    public ResponseEntity<Response> acceptInvitation(@PathVariable String id, Principal principal) throws AppException {
        String userId = principal.getName();
        invitationService.acceptInvitation(userId, id);

    }
}
