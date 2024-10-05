package com.vapps.expense.repository.cache;

import com.vapps.expense.common.dto.ExpenseStatsDTO;
import com.vapps.expense.model.ExpenseStats;
import com.vapps.expense.repository.ExpenseStatsRepository;
import com.vapps.expense.repository.mongo.ExpenseStatsMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ExpenseStatsCacheRepository implements ExpenseStatsRepository {

	@Autowired
	private ExpenseStatsMongoRepository expenseStatsRepository;

	@Override
	@CacheEvict(value = "expense_stats", allEntries = true)
	public ExpenseStats save(ExpenseStats stats) {
		return expenseStatsRepository.save(stats);
	}

	@Override
	@Cacheable(value = "expense_stats", unless = "#result == null", key = "'expense_stats_owner_' + #ownerId + '_' + #type")
	public Optional<ExpenseStats> findByOwnerIdAndType(String ownerId, ExpenseStatsDTO.ExpenseStatsType type) {
		return expenseStatsRepository.findByOwnerIdAndType(ownerId, type);
	}

	@Override
	@Cacheable(value = "expense_stats", unless = "#result == null", key = "'expense_stats_' + #id")
	public Optional<ExpenseStats> findById(String id) {
		return expenseStatsRepository.findById(id);
	}
}
