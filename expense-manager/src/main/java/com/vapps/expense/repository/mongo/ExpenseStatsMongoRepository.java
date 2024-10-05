package com.vapps.expense.repository.mongo;

import com.vapps.expense.common.dto.ExpenseStatsDTO;
import com.vapps.expense.model.ExpenseStats;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ExpenseStatsMongoRepository extends MongoRepository<ExpenseStats, String> {

	Optional<ExpenseStats> findByOwnerIdAndType(String ownerId, ExpenseStatsDTO.ExpenseStatsType type);
}
