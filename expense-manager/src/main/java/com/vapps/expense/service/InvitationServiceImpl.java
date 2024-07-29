package com.vapps.expense.service;

import com.vapps.expense.annotation.InvitationIdValidator;
import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.EmailService;
import com.vapps.expense.common.service.InvitationService;
import com.vapps.expense.model.Invitation;
import com.vapps.expense.repository.InvitationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private EmailService emailService;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvitationServiceImpl.class);

    @Override
    @UserIdValidator(positions = 0)
    public InvitationDTO sendInvitation(String userId, InvitationDTO invitation) throws AppException {
        checkDuplicateInvitation(userId, invitation);
        Invitation invitationModel = Invitation.build(invitation);
        Invitation savedInvitation = invitationRepository.save(invitationModel);
        if (savedInvitation == null) {
            throw new AppException("Error while saving invitation!");
        }
        emailService.sendEmail(savedInvitation.getRecipient().getEmail(), savedInvitation.getTitle(),
                savedInvitation.getContent());
        return savedInvitation.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    @InvitationIdValidator(positions = 1)
    public void acceptInvitation(String userId, String id) throws AppException {
        InvitationDTO invitation = getInvitation(id).get();
        if (!invitation.getRecipient().getId().equals(userId)) {
            LOGGER.error("User {} trying to accept user {}'s invitation {}", userId, invitation.getRecipient().getId(),
                    invitation.getId());
            throw new AppException(HttpStatus.FORBIDDEN.value(), "You can't accept other's invitation!");
        }
        switch (invitation.getType()) {
            case FAMILY_INVITE -> handleFamilyInvitationAccept(invitation);
            default -> throw new AppException("Invitation type not handled!");
        }
        invitationRepository.deleteById(id);
    }

    @Override
    @UserIdValidator(positions = 0)
    @InvitationIdValidator(positions = 1)
    public void rejectInvitation(String userId, String id) throws AppException {

    }

    @Override
    public Optional<InvitationDTO> getInvitation(String id) throws AppException {
        Optional<Invitation> invitation = invitationRepository.findById(id);
        if (invitation.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(invitation.get().toDTO());
    }

    private void checkDuplicateInvitation(String userId, InvitationDTO invitation) throws AppException {
        if (invitationRepository.findByRecipientIdAndFromIdAndType(invitation.getRecipient().getId(), userId,
                invitation.getType()).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invitation already exists!");
        }
    }

    private void handleFamilyInvitationAccept(InvitationDTO invitation) throws AppException {

    }
}
