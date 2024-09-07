package com.vapps.expense.service;

import com.vapps.expense.annotation.ExpenseIdValidator;
import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.CategoryService;
import com.vapps.expense.common.service.ExpenseService;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.service.UserService;
import com.vapps.expense.model.Category;
import com.vapps.expense.model.Expense;
import com.vapps.expense.model.Family;
import com.vapps.expense.model.User;
import com.vapps.expense.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Override
    @UserIdValidator(positions = 0)
    public ExpenseDTO addExpense(String userId, ExpenseCreationPayload payload) throws AppException {
        checkCurrency(payload.getCurrency());
        validateExpenseData(userId, payload.getCategoryId(), payload.getType(), payload.getFamilyId());
        checkExpenseAccess(userId, payload);

        Expense expense = new Expense();
        expense.setCreatedBy(User.build(userService.getUser(userId).get()));
        expense.setName(payload.getName());
        expense.setDescription(payload.getDescription());
        expense.setAmount(payload.getAmount());
        expense.setCurrency(payload.getCurrency());
        expense.setType(payload.getType());
        expense.setOwnerId(userId);
        expense.setCategory(Category.build(categoryService.getCategory(userId, payload.getCategoryId()).get()));
        if (expense.getType() == ExpenseDTO.ExpenseType.FAMILY) {
            FamilyDTO family = familyService.getUserFamily(userId).get();
            expense.setOwnerId(family.getId());
            expense.setFamily(Family.build(family));
        }
        expense.setTime(payload.getTime());
        if (expense.getTime() == null) {
            expense.setTime(LocalDateTime.now());
        }
        if (payload.getType() == ExpenseDTO.ExpenseType.PERSONAL && payload.getFamilyId() != null) {
            expense.setFamily(Family.build(familyService.getFamilyById(userId, payload.getFamilyId()).get()));
        }

        expense = expenseRepository.save(expense);
        if (expense == null) {
            throw new AppException("Error while saving expense!");
        }
        return expense.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    @ExpenseIdValidator(userIdPosition = 0, positions = 1)
    public ExpenseDTO updateExpense(String userId, String expenseId, ExpenseUpdatePayload payload) throws AppException {
        if (payload.getCurrency() != null) {
            checkCurrency(payload.getCurrency());
        }
        Expense expense = Expense.build(getExpense(userId, expenseId).get());

        if (payload.getName() != null) {
            expense.setName(payload.getName());
        }
        if (payload.getDescription() != null) {
            expense.setDescription(payload.getDescription());
        }
        if (payload.getTime() != null) {
            expense.setTime(payload.getTime());
        }
        if (payload.getCurrency() != null) {
            expense.setCurrency(payload.getCurrency());
        }
        if (payload.getAmount() > 0) {
            expense.setAmount(payload.getAmount());
        }
        if (payload.getCategoryId() != null && !expense.getCategory().getId().equals(payload.getCategoryId())) {
            validateExpenseData(userId, payload.getCategoryId(), expense.getType(), expense.getFamily().getId());
            expense.setCategory(Category.build(categoryService.getCategory(userId, payload.getCategoryId()).get()));
        }
        expense = expenseRepository.update(expense);
        if (expense == null) {
            throw new AppException("Error while updating expense!");
        }
        return expense.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    public Optional<ExpenseDTO> getExpense(String userId, String expenseId) {
        Optional<Expense> expense = expenseRepository.findByIdAndOwnerId(expenseId, userId);
        if (expense.isEmpty()) {
            Optional<FamilyDTO> familyDTO = familyService.getUserFamily(userId);
            if (familyDTO.isEmpty()) {
                return Optional.empty();
            }
            expense = expenseRepository.findByIdAndOwnerId(expenseId, familyDTO.get().getId());
        }
        return expense.map(Expense::toDTO);
    }

    @Override
    @UserIdValidator(positions = 0)
    @ExpenseIdValidator(userIdPosition = 0, positions = 1)
    public void deleteExpense(String userId, String expenseId) throws AppException {
        ExpenseDTO expense = getExpense(userId, expenseId).get();
        if (expense.getType() == ExpenseDTO.ExpenseType.FAMILY && familyService.getUserRoleInFamily(userId,
                expense.getOwnerId()) == FamilyMemberDTO.Role.MEMBER) {
            throw new AppException(HttpStatus.FORBIDDEN.value(), "You are not allowed to delete family's expense!");
        }
        expenseRepository.deleteById(expenseId);
    }

    private void checkCurrency(String currency) throws AppException {
        if (Currency.getAvailableCurrencies().stream().noneMatch(curr -> curr.getCurrencyCode().equals(currency))) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid currency " + currency);
        }
    }

    private void checkExpenseAccess(String userId, ExpenseCreationPayload expense) throws AppException {
        if (expense.getType() == ExpenseDTO.ExpenseType.FAMILY) {
            Optional<FamilyDTO> familyDTO = familyService.getUserFamily(userId);
            if (familyDTO.isEmpty()) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(),
                        "You should be in a family to create family type expense!");
            }
            if (familyService.getUserRoleInFamily(userId, familyDTO.get().getId()) == FamilyMemberDTO.Role.MEMBER) {
                throw new AppException(HttpStatus.FORBIDDEN.value(),
                        "You are not allowed to add expense in behalf of your family!");
            }
        } else if (expense.getFamilyId() != null && familyService.getFamilyById(userId, expense.getFamilyId())
                .isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid family id " + expense.getFamilyId());
        }
    }

    private void validateExpenseData(String userId, String categoryId, ExpenseDTO.ExpenseType type, String familyId) throws AppException {
        if (categoryId == null) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Category Id is required!");
        }
        Optional<CategoryDTO> category = categoryService.getCategory(userId, categoryId);
        if (category.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Category not exists!");
        }
        Optional<FamilyDTO> family = familyService.getUserFamily(userId);
        if (type == ExpenseDTO.ExpenseType.FAMILY) {
            if (family.isEmpty()) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "You should be in a family");
            }
            if (!category.get().getOwnerId().equals(family.get().getId())) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Given category not belongs to the family!");
            }
        } else {
            if (familyId != null && (family.isEmpty() || !family.get().getId().equals(familyId))) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Invalid familyId");
            }
            if (category.get().getType() == CategoryDTO.CategoryType.FAMILY && familyId == null) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "Only personal categories can be used for personal expenses!");
            }
        }
    }
}
