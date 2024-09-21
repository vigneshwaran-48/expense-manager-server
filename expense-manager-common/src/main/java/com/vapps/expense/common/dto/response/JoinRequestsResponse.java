package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.JoinRequestDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class JoinRequestsResponse extends Response {

	private List<JoinRequestDTO> requests;

	public JoinRequestsResponse(int status, String message, LocalDateTime time, String path,
			List<JoinRequestDTO> requests) {
		super(status, message, time, path);
		this.requests = requests;
	}
}
