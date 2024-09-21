package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.FamilyMemberDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FamilyRoleResponse extends Response {
	private FamilyMemberDTO.Role role;

	public FamilyRoleResponse(int status, String message, LocalDateTime time, String path, FamilyMemberDTO.Role role) {
		super(status, message, time, path);
		this.role = role;
	}
}
