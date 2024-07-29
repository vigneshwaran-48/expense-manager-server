package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.common.exception.AppException;

import java.util.Optional;

public interface InvitationService {

    InvitationDTO sendInvitation(String userId, InvitationDTO invitation) throws AppException;

    void acceptInvitation(String userId, String id) throws AppException;

    void rejectInvitation(String userId, String id) throws AppException;

    Optional<InvitationDTO> getInvitation(String id) throws AppException;

}
