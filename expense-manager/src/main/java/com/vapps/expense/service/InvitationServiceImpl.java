package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.InvitationService;
import com.vapps.expense.model.Invitation;
import com.vapps.expense.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Override
    @UserIdValidator(positions = 0)
    public InvitationDTO addInvitation(String userId, InvitationDTO invitation) throws AppException {
        checkDuplicateInvitation(userId, invitation);
        Invitation invitationModel = Invitation.build(invitation);
        Invitation savedInvitation = invitationRepository.save(invitationModel);
        if (savedInvitation == null) {
            throw new AppException("Error while saving invitation!");
        }
        return savedInvitation.toDTO();
    }

    private void checkDuplicateInvitation(String userId, InvitationDTO invitation) throws AppException {
        if (invitationRepository.findByRecipientIdAndFromIdAndType(invitation.getRecipient().getId(), userId,
                invitation.getType()).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invitation already exists!");
        }
    }
}
