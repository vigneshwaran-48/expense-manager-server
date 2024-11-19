package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.SettingsDTO;
import com.vapps.expense.common.dto.UserDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.SettingsService;
import com.vapps.expense.common.service.UserService;
import com.vapps.expense.model.Settings;
import com.vapps.expense.model.User;
import com.vapps.expense.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserService userService;

    @Override
    @UserIdValidator(positions = 0)
    public SettingsDTO getSettings(String userId) throws AppException {
        Optional<Settings> settings = settingsRepository.findByUserId(userId);
        if (settings.isEmpty()) {
            Settings settingsModel = new Settings();
            UserDTO user = userService.getUser(userId).get();
            settingsModel.setUser(User.build(user));
            settingsModel = settingsRepository.save(settingsModel);
            if (settingsModel == null) {
                throw new AppException("Error while creating settings for user");
            }
            settings = Optional.of(settingsModel);
        }
        return settings.get().toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    public SettingsDTO updateSettings(String userId, SettingsDTO settings) throws AppException {
        SettingsDTO existingSettings = getSettings(userId);
        if (existingSettings.getTheme() != settings.getTheme()) {
            existingSettings.setTheme(settings.getTheme());
        }
        if (existingSettings.isDarkMode() != settings.isDarkMode()) {
            existingSettings.setDarkMode(settings.isDarkMode());
        }
        User user = User.build(userService.getUser(userId).get());
        Settings updatedSettings = settingsRepository.update(Settings.build(existingSettings, user));
        if (updatedSettings == null) {
            throw new AppException("Error while updating settings");
        }
        return updatedSettings.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    public void deleteSettings(String userId) {
        settingsRepository.deleteByUserId(userId);
    }
}