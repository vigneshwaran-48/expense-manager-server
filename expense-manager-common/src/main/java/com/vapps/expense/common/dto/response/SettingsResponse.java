package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.SettingsDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SettingsResponse extends Response {

    private SettingsDTO settings;

    public SettingsResponse(int status, String message, LocalDateTime time, String path, SettingsDTO settings) {
        super(status, message, time, path);
        this.settings = settings;
    }
}
