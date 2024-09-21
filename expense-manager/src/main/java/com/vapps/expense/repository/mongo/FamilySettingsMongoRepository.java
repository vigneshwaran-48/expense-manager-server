package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.FamilySettings;
import jakarta.transaction.Transactional;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FamilySettingsMongoRepository extends MongoRepository<FamilySettings, String> {

	@Transactional
	void deleteByFamilyId(String familyId);

	Optional<FamilySettings> findByFamilyId(String familyId);
}
