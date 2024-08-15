package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.ExpenseCreationPayload;
import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.exception.AppException;

public interface ExpenseService {

    ExpenseDTO addExpense(String userId, ExpenseCreationPayload payload) throws AppException;
}
