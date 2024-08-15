package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.ExpenseCreationPayload;
import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.dto.ExpenseUpdatePayload;
import com.vapps.expense.common.exception.AppException;

import java.util.Optional;

public interface ExpenseService {

    ExpenseDTO addExpense(String userId, ExpenseCreationPayload payload) throws AppException;

    ExpenseDTO updateExpense(String userId, String expenseId, ExpenseUpdatePayload payload) throws AppException;

    Optional<ExpenseDTO> getExpense(String userId, String expenseId);
}
