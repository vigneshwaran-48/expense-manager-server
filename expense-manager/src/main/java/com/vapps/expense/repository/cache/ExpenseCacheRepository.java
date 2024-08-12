package com.vapps.expense.repository.cache;

import com.vapps.expense.model.Expense;
import com.vapps.expense.repository.ExpenseRepository;
import com.vapps.expense.repository.mongo.ExpenseMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ExpenseCacheRepository implements ExpenseRepository {

    @Autowired
    private ExpenseMongoRepository expenseRepository;

    @Override
    @Cacheable(value = "expense", key = "'expense_' + #id")
    public Optional<Expense> findById(String id) {
        return expenseRepository.findById(id);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "expenseByOwnerId"), @CacheEvict(value = "expenseByFamilyId") })
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "expenseByOwnerId"), @CacheEvict(value = "expenseByFamilyId") })
    @CachePut(value = "expense", key = "'expense_' + #expense.getId()")
    public Expense update(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    @Cacheable(value = "expenseByOwnerId", key = "'expense_owner_id_' + #ownerId")
    public List<Expense> findByOwnerIdAndFamilyIsNull(String ownerId) {
        return expenseRepository.findByOwnerIdAndFamilyIsNull(ownerId);
    }

    @Override
    @Cacheable(value = "expenseByFamilyId", key = "'expense_family_id_' + #familyId")
    public List<Expense> findByFamilyId(String familyId) {
        return expenseRepository.findByFamilyId(familyId);
    }
}