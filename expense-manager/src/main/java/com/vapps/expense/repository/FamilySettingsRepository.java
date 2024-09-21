package com.vapps.expense.repository;

import com.vapps.expense.model.FamilySettings;

import java.util.Optional;

public interface FamilySettingsRepository {

	FamilySettings save(FamilySettings familySettings);

	FamilySettings update(FamilySettings familySettings);

	void deleteByFamilyId(String familyId);

	Optional<FamilySettings> findByFamilyId(String familyId);
}
