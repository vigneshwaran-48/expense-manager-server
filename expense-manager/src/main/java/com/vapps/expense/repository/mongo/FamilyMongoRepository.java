package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.Family;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FamilyMongoRepository extends MongoRepository<Family, String> {
}
