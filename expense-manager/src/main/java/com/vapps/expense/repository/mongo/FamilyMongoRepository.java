package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.Family;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FamilyMongoRepository extends MongoRepository<Family, String> {
    Optional<Family> findByCreatedById(String createdById);
}
