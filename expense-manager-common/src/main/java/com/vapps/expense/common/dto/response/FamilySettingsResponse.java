package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.FamilySettingsDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FamilySettingsResponse extends Response {

	private FamilySettingsDTO settings;

	public FamilySettingsResponse(int status, String message, LocalDateTime time, String path, FamilySettingsDTO settings) {
		super(status, message, time, path);
		this.settings = settings;
	}
}
