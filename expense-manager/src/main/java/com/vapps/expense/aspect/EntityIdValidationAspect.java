package com.vapps.expense.aspect;

import com.vapps.expense.annotation.*;
import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
public class EntityIdValidationAspect {

    @Autowired
    private UserService userService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ExpenseService expenseService;

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

        String userId = (String) joinPoint.getArgs()[invitationIdValidator.userIdPosition()];

        if (userId == null) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "User Id is required!");
        }

        Object[] args = joinPoint.getArgs();

        for (int position : positionsToCheck) {
            String id = (String) args[position];

            if (id == null) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invitation Id is required!");
            }

            Optional<InvitationDTO> invitation = invitationService.getInvitation(userId, id);
            if (invitation.isEmpty()) {
                LOGGER.error("Invitation {} not exists", id);
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invitation " + id + " not exists!");
            }
        }
    }

    @Before("@annotation(categoryIdValidator)")
    public void checkCategoryExists(JoinPoint joinPoint, CategoryIdValidator categoryIdValidator) throws AppException {
        int[] positionsToCheck = categoryIdValidator.positions();
        String userId = (String) joinPoint.getArgs()[categoryIdValidator.userIdPosition()];

        if (userId == null) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "User Id is required!");
        }
        Object[] args = joinPoint.getArgs();
        for (int position : positionsToCheck) {
            String id = (String) args[position];
            if (id == null) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Category Id is required!");
            }
            Optional<CategoryDTO> category = categoryService.getCategory(userId, id);
            if (category.isEmpty()) {
                LOGGER.error("Category {} not exists", id);
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Category " + id + " not exists!");
            }
        }
    }

    @Before("@annotation(expenseIdValidator)")
    public void checkExpenseExists(JoinPoint joinPoint, ExpenseIdValidator expenseIdValidator) throws AppException {
        int[] positionsToCheck = expenseIdValidator.positions();
        String userId = (String) joinPoint.getArgs()[expenseIdValidator.userIdPosition()];

        if (userId == null) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "User Id is required!");
        }
        Object[] args = joinPoint.getArgs();
        for (int position : positionsToCheck) {
            String id = (String) args[position];
            if (id == null) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Expense Id is required!");
            }
            Optional<ExpenseDTO> expense = expenseService.getExpense(userId, id);
            if (expense.isEmpty()) {
                LOGGER.error("Expense {} not exists", id);
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Expense " + id + " not exists!");
            }
        }
    }
}
