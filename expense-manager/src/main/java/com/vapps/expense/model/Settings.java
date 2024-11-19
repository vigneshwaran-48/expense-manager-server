package com.vapps.expense.model;

import com.vapps.expense.common.dto.SettingsDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@Document
public class Settings {

    @Id
    private String id;
    private boolean isDarkMode;
    private SettingsDTO.Theme theme;

    @DocumentReference
    private User user;

    public SettingsDTO toDTO() {
        SettingsDTO settings = new SettingsDTO();
        settings.setId(id);
        settings.setTheme(theme);
        settings.setDarkMode(isDarkMode);
        return settings;
    }

    public static Settings build(SettingsDTO settingsDTO, User user) {
        Settings settings = new Settings();
        settings.setId(settingsDTO.getId());
        settings.setUser(user);
        settings.setDarkMode(settingsDTO.isDarkMode());
        settings.setTheme(settingsDTO.getTheme());
        return settings;
    }
}
