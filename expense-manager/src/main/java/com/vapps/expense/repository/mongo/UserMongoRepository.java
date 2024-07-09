package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<User, String> {
}
