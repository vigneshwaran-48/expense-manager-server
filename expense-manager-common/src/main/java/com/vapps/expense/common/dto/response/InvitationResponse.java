package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.InvitationDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvitationResponse extends Response {
	private InvitationDTO invitation;

	public InvitationResponse(int status, String message, LocalDateTime time, String path,
			InvitationDTO invitation) {
		super(status, message, time, path);
		this.invitation = invitation;
	}
}
