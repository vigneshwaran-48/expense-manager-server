package com.vapps.expense.repository;

import com.vapps.expense.model.Settings;

import java.util.Optional;

public interface SettingsRepository {

    Settings save(Settings settings);

    Settings update(Settings settings);

    void deleteByUserId(String userId);

    Optional<Settings> findByUserId(String userId);
}
