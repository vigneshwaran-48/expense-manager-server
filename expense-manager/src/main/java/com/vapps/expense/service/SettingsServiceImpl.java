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
            settings =
                    Optional.of(Settings.build(createSettings(userId), User.build(userService.getUser(userId).get())));
        }
        return settings.get().toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    public SettingsDTO updateSettings(String userId, SettingsDTO settings) throws AppException {
        SettingsDTO existingSettings = getSettings(userId);
        if (settings.getTheme() == null) {
            settings.setTheme(existingSettings.getTheme());
        }
        settings.setId(existingSettings.getId());
        User user = User.build(userService.getUser(userId).get());
        Settings updatedSettings = settingsRepository.update(Settings.build(settings, user));
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

    @Override
    @UserIdValidator(positions = 0)
    public SettingsDTO createSettings(String userId) throws AppException {
        Settings settingsModel = new Settings();
        UserDTO user = userService.getUser(userId).get();
        settingsModel.setUser(User.build(user));
        settingsModel = settingsRepository.save(settingsModel);
        if (settingsModel == null) {
            throw new AppException("Error while creating settings for user");
        }
        return settingsModel.toDTO();
    }
}
