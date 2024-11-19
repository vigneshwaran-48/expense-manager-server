package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.SettingsDTO;
import com.vapps.expense.common.exception.AppException;

public interface SettingsService {

    SettingsDTO getSettings(String userId) throws AppException;

    SettingsDTO updateSettings(String userId, SettingsDTO settings) throws AppException;

    void deleteSettings(String userId) throws AppException;
}
