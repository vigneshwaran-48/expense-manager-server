package com.vapps.expense.repository.cache;

import com.vapps.expense.model.FamilySettings;
import com.vapps.expense.repository.FamilySettingsRepository;
import com.vapps.expense.repository.mongo.FamilySettingsMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FamilySettingsCacheRepository implements FamilySettingsRepository {

	@Autowired
	private FamilySettingsMongoRepository familySettingsRepository;

	@Override
	@Cacheable(value = "familySettings", key = "'family_settings_' + #familySettings.getFamily().getId()")
	public FamilySettings save(FamilySettings familySettings) {
		return familySettingsRepository.save(familySettings);
	}

	@Override
	@CachePut(value = "familySettings", key = "'family_settings_' + #familySettings.getFamily().getId()")
	public FamilySettings update(FamilySettings familySettings) {
		return familySettingsRepository.save(familySettings);
	}

	@Override
	@CacheEvict(value = "familySettings", key = "'family_settings_' + #familyId")
	public void deleteByFamilyId(String familyId) {
		familySettingsRepository.deleteByFamilyId(familyId);
	}

	@Override
	@Cacheable(value = "familySettings", key = "'family_settings_' + #familyId")
	public Optional<FamilySettings> findByFamilyId(String familyId) {
		return familySettingsRepository.findByFamilyId(familyId);
	}
}
