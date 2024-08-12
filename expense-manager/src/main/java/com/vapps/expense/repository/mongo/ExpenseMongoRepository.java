package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExpenseMongoRepository extends MongoRepository<Expense, String> {

    List<Expense> findByOwnerIdAndFamilyIsNull(String ownerId);

    List<Expense> findByFamilyId(String familyId);

}
