package com.vapps.expense.controller;

import com.vapps.expense.common.dto.SettingsDTO;
import com.vapps.expense.common.dto.response.SettingsResponse;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.SettingsService;
import com.vapps.expense.common.util.Endpoints;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping(Endpoints.SETTINGS_API)
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping
    public ResponseEntity<SettingsResponse> getSettings(Principal principal, HttpServletRequest request)
            throws AppException {
        String userId = principal.getName();
        SettingsDTO settingsDTO = settingsService.getSettings(userId);

        return ResponseEntity.ok(
                new SettingsResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
                        settingsDTO));
    }

    @PatchMapping
    public  ResponseEntity<SettingsResponse> updateSettings(@RequestBody SettingsDTO settings, Principal principal,
            HttpServletRequest request) throws AppException {
        String userId = principal.getName();
        settings = settingsService.updateSettings(userId, settings);

        return ResponseEntity.ok(
                new SettingsResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
                        settings));
    }
}
