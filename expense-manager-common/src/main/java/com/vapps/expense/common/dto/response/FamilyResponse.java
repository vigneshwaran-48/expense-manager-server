package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.FamilyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FamilyResponse extends Response {

	private FamilyDTO family;

	public FamilyResponse(int status, String message, LocalDateTime time, String path, FamilyDTO family) {
		super(status, message, time, path);
		this.family = family;
	}
}
