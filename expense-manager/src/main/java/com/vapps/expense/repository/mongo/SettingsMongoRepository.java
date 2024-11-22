package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.Settings;
import jakarta.transaction.Transactional;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SettingsMongoRepository extends MongoRepository<Settings, String> {

    @Transactional
    void deleteByUserId(String userId);

    Optional<Settings> findByUserId(String userId);
}

