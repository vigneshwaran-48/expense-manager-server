package com.vapps.expense.service;

import com.vapps.expense.annotation.InvitationIdValidator;
import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.EmailService;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.service.InvitationService;
import com.vapps.expense.model.Invitation;
import com.vapps.expense.repository.InvitationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FamilyService familyService;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvitationServiceImpl.class);

    @Override
    @UserIdValidator(positions = 0)
    public InvitationDTO sendInvitation(String userId, InvitationDTO invitation, Context context) throws AppException {
        checkDuplicateInvitation(userId, invitation);
        Invitation invitationModel = Invitation.build(invitation);
        invitationModel.setSentTime(LocalDateTime.now());
        invitationModel.setStatus(InvitationDTO.InvitationStatus.ACTIVE);
        Invitation savedInvitation = invitationRepository.save(invitationModel);
        if (savedInvitation == null) {
            throw new AppException("Error while saving invitation!");
        }
        context.setVariable("title", invitation.getTitle());
        context.setVariable("content", invitation.getContent());
        emailService.sendEmail(savedInvitation.getRecipient().getEmail(), savedInvitation.getTitle(),
                "invitation-template", context);
        return savedInvitation.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    @InvitationIdValidator(userIdPosition = 0, positions = 1)
    public void acceptInvitation(String userId, String id) throws AppException {
        InvitationDTO invitation = getInvitation(userId, id).get();
        if (!invitation.getRecipient().getId().equals(userId)) {
            LOGGER.error("User {} trying to accept user {}'s invitation {}", userId, invitation.getRecipient().getId(),
                    invitation.getId());
            throw new AppException(HttpStatus.FORBIDDEN.value(), "You can't accept other's invitation!");
        }
        if (invitation.getStatus() == InvitationDTO.InvitationStatus.REVOKED) {
            invitationRepository.deleteById(id); // Deleting it here once informed that it has been revoked!
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invitation has been revoked by the sender!");
        }
        switch (invitation.getType()) {
            case FAMILY_INVITE -> handleFamilyInvitationAccept(invitation);
            default -> throw new AppException("Invitation type not handled!");
        }
        invitationRepository.deleteById(id);
    }

    @Override
    @UserIdValidator(positions = 0)
    @InvitationIdValidator(userIdPosition = 0, positions = 1)
    public void rejectInvitation(String userId, String id) throws AppException {

    }

    @Override
    @UserIdValidator(positions = 0)
    public Optional<InvitationDTO> getInvitation(String userId, String id) throws AppException {
        Optional<Invitation> invitation = invitationRepository.findByIdAndRecipientIdOrFromId(id, userId, userId);
        if (invitation.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(invitation.get().toDTO());
    }

    @Override
    @UserIdValidator(positions = 0)
    public List<InvitationDTO> getAllInvitations(String userId) throws AppException {
        return invitationRepository.findByRecipientIdAndStatus(userId, InvitationDTO.InvitationStatus.ACTIVE)
                .stream().map(Invitation::toDTO).toList();
    }

    @Override
    @UserIdValidator(positions = 0)
    public List<InvitationDTO> getAllSentInvitations(String userId) throws AppException {
        return invitationRepository.findByFromIdAndStatus(userId, InvitationDTO.InvitationStatus.ACTIVE)
                .stream().map(Invitation::toDTO).toList();
    }

    @Override
    @UserIdValidator(positions = 0)
    @InvitationIdValidator(userIdPosition = 0, positions = 1)
    public void resendInvitation(String userId, String invitationId) throws AppException {
        InvitationDTO invitationDTO = getInvitation(userId, invitationId).get();
        if (invitationDTO.getStatus() == InvitationDTO.InvitationStatus.REVOKED) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invitation has been revoked already!");
        }
        if (!invitationDTO.getFrom().getId().equals(userId)) {
            throw new AppException(HttpStatus.FORBIDDEN.value(), "Only invitation sent people can resend it!");
        }
        switch (invitationDTO.getType()) {
            case FAMILY_INVITE:
                String familyId = (String) invitationDTO.getProperties().get(InvitationDTO.InvitationProps.FAMILY_ID);
                Optional<FamilyDTO> family = familyService.getFamilyById(userId, familyId);
                if (family.isEmpty()) {
                    throw new AppException(HttpStatus.BAD_REQUEST.value(), "Currently the family not exists!");
                }
                Context context = new Context();
                context.setVariable("familyDescription", family.get().getDescription());
                context.setVariable("familyName", family.get().getName());
                context.setVariable("title", invitationDTO.getTitle());
                context.setVariable("content", invitationDTO.getContent());
                emailService.sendEmail(invitationDTO.getRecipient().getEmail(), invitationDTO.getTitle(),
                        "invitation-template", context);
                break;
            default:
                LOGGER.info("Unknown Invitation type!");
        }
        Invitation model = Invitation.build(invitationDTO);
        model.setSentTime(LocalDateTime.now());
        model = invitationRepository.update(model);
        if (model == null) {
            throw new AppException("Error while revoking the invitation!");
        }
    }

    @Override
    @UserIdValidator(positions = 0)
    @InvitationIdValidator(userIdPosition = 0, positions = 1)
    public InvitationDTO revokeInvitation(String userId, String invitationId) throws AppException {
        InvitationDTO invitationDTO = getInvitation(userId, invitationId).get();
        if (!invitationDTO.getFrom().getId().equals(userId)) {
            throw new AppException(HttpStatus.FORBIDDEN.value(), "Only invitation sent people can revoke it!");
        }
        invitationDTO.setStatus(InvitationDTO.InvitationStatus.REVOKED);
        Invitation model = Invitation.build(invitationDTO);
        model = invitationRepository.update(model);
        if (model == null) {
            throw new AppException("Error while revoking the invitation!");
        }
        return model.toDTO();
    }

    private void checkDuplicateInvitation(String userId, InvitationDTO invitation) throws AppException {
        if (invitationRepository.findByRecipientIdAndFromIdAndType(invitation.getRecipient().getId(), userId,
                invitation.getType()).isPresent()) {
            LOGGER.info("Invitation {} is already present", invitationRepository.findByRecipientIdAndFromIdAndType(invitation.getRecipient().getId(), userId, invitation.getType()).get().getId());
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invitation already exists!");
        }
    }

    private void handleFamilyInvitationAccept(InvitationDTO invitation) throws AppException {
        String familyId = (String) invitation.getProperties().get(InvitationDTO.InvitationProps.FAMILY_ID);
        String roleStr = (String) invitation.getProperties().get(InvitationDTO.InvitationProps.ROLE);
        if (Arrays.stream(FamilyMemberDTO.Role.values()).filter(role -> role.name().equals(roleStr)).findFirst()
                .isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Role is missing or invalid!");
        }
        FamilyMemberDTO.Role role = FamilyMemberDTO.Role.valueOf(roleStr);
        if (familyId == null) {
            throw new AppException("Family id missing in the invitation!");
        }
        familyService.addMember(invitation.getFrom().getId(), familyId, invitation.getRecipient().getId(), role);
    }
}
