package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse extends Response {
	private UserDTO user;

	public UserResponse(int status, String message, LocalDateTime time, String path, UserDTO user) {
		super(status, message, time, path);
		this.user = user;
	}
}
