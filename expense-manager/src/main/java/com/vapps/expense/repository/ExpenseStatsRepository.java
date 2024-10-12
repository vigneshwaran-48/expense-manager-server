package com.vapps.expense.repository;

import com.vapps.expense.common.dto.ExpenseStatsDTO;
import com.vapps.expense.model.ExpenseStats;

import java.util.Optional;

public interface ExpenseStatsRepository {

	ExpenseStats save(ExpenseStats stats);

	Optional<ExpenseStats> findByOwnerIdAndType(String ownerId, ExpenseStatsDTO.ExpenseStatsType type);

	Optional<ExpenseStats> findById(String id);

	ExpenseStats update(ExpenseStats stats);
}
