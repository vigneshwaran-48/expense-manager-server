package com.vapps.expense.common.dto;

import lombok.Data;

@Data
public class SettingsDTO {

    public enum Theme {
        BLUE,
        RED,
        GREEN
    }

    private String id;
    private Theme theme;
    private boolean isDarkMode;

}
