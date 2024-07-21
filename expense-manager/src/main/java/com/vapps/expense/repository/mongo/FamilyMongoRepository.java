package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.Family;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyMongoRepository extends JpaRepository<Family, String> {
}
