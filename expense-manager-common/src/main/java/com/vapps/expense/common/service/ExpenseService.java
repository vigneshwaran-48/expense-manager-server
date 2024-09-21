package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.ExpenseCreationPayload;
import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.dto.ExpenseFilter;
import com.vapps.expense.common.dto.ExpenseUpdatePayload;
import com.vapps.expense.common.exception.AppException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {

	ExpenseDTO addExpense(String userId, ExpenseCreationPayload payload, MultipartFile[] invoices) throws AppException;

	ExpenseDTO updateExpense(String userId, String expenseId, ExpenseUpdatePayload payload, MultipartFile[] invoices)
			throws AppException;

	Optional<ExpenseDTO> getExpense(String userId, String expenseId);

	void deleteExpense(String userId, String expenseId) throws AppException;

	List<ExpenseDTO> getAllExpense(String userId, ExpenseFilter filter) throws AppException;
}
