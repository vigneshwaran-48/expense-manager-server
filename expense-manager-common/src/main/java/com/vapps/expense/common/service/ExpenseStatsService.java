package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.dto.ExpenseStatsDTO;
import com.vapps.expense.common.exception.AppException;

import java.util.Optional;

public interface ExpenseStatsService {

	ExpenseStatsDTO createStats(String userId, String ownerId, ExpenseStatsDTO.ExpenseStatsType type)
			throws AppException;

	Optional<ExpenseStatsDTO> getPersonalStats(String userId)
			throws AppException;

	Optional<ExpenseStatsDTO> getFamilyStats(String userId) throws AppException;

	void addExpense(ExpenseDTO expense) throws AppException;
}
