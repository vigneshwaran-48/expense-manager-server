package com.vapps.expense.repository;

import com.vapps.expense.model.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {

    Optional<Expense> findById(String id);

    Expense save(Expense expense);

    Expense update(Expense expense);

    List<Expense> findByOwnerIdAndFamilyIsNull(String ownerId);

    List<Expense> findByFamilyId(String familyId);

}
