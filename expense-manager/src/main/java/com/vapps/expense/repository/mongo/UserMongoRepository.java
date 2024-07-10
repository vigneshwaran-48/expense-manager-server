package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
}
