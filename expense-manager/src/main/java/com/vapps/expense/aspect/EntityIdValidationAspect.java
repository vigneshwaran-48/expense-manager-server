package com.vapps.expense.aspect;

import com.vapps.expense.annotation.FamilyIdValidator;
import com.vapps.expense.annotation.InvitationIdValidator;
import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.InvitationDTO;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.service.InvitationService;
import com.vapps.expense.common.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Aspect
public class EntityIdValidationAspect {

    @Autowired
    private UserService userService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private InvitationService invitationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityIdValidationAspect.class);

    @Before("@annotation(userIdValidator)")
    public void checkUserExists(JoinPoint joinPoint, UserIdValidator userIdValidator) throws AppException {
        int[] positionsToCheck = userIdValidator.positions();

        Object[] args = joinPoint.getArgs();

        for (int position : positionsToCheck) {
            String id = (String) args[position];

            if (id == null) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "User Id is required!");
            }

            Optional<UserDTO> user = userService.getUser(id);
            if (user.isEmpty()) {
                LOGGER.error("User {} not exists", id);
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "User " + id + " not exists!");
            }
        }
    }

    @Before("@annotation(familyIdValidator)")
    public void checkFamilyExists(JoinPoint joinPoint, FamilyIdValidator familyIdValidator) throws AppException {
        int[] positionsToCheck = familyIdValidator.positions();
        String userId = (String) joinPoint.getArgs()[familyIdValidator.userIdPosition()];

        if (userId == null) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "User Id is required!");
        }

        Object[] args = joinPoint.getArgs();

        for (int position : positionsToCheck) {
            String id = (String) args[position];

            if (id == null) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Family Id is required!");
            }

            Optional<FamilyDTO> family = familyService.getFamilyById(userId, id);
            if (family.isEmpty()) {
                LOGGER.error("Family {} not exists", id);
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Family " + id + " not exists!");
            }
        }
    }

    @Before("@annotation(invitationIdValidator)")
    public void checkInvitationExists(JoinPoint joinPoint, InvitationIdValidator invitationIdValidator)
            throws AppException {
        int[] positionsToCheck = invitationIdValidator.positions();

        Object[] args = joinPoint.getArgs();

        for (int position : positionsToCheck) {
            String id = (String) args[position];

            if (id == null) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invitation Id is required!");
            }

            Optional<InvitationDTO> invitation = invitationService.getInvitation(id);
            if (invitation.isEmpty()) {
                LOGGER.error("Invitation {} not exists", id);
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invitation " + id + " not exists!");
            }
        }
    }
}
