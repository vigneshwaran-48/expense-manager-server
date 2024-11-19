package com.vapps.expense.repository.cache;

import com.vapps.expense.model.Settings;
import com.vapps.expense.repository.SettingsRepository;
import com.vapps.expense.repository.mongo.SettingsMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SettingsCacheRepository implements SettingsRepository {

    @Autowired
    private SettingsMongoRepository settingsRepository;

    @Override
    @CacheEvict(value = "settings", allEntries = true)
    public Settings save(Settings settings) {
        return settingsRepository.save(settings);
    }

    @Override
    @CachePut(value = "settings", key = "'settings_' + #settings.getUser().getId()")
    public Settings update(Settings settings) {
        return settingsRepository.save(settings);
    }

    @Override
    @CacheEvict(value = "settings", key = "'settings_' + #userId")
    public void deleteByUserId(String userId) {
        settingsRepository.deleteByUserId(userId);
    }

    @Override
    @Cacheable(value = "settings", key = "'settings_' + #userId")
    public Optional<Settings> findByUserId(String userId) {
        return settingsRepository.findByUserId(userId);
    }
}
