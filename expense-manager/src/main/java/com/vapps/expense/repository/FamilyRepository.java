package com.vapps.expense.repository;

import com.vapps.expense.model.Family;

import java.util.Optional;

public interface FamilyRepository {

    Optional<Family> findById(String id);

    Family save(Family family);

    Family update(Family family);

    void deleteById(String id);
}
