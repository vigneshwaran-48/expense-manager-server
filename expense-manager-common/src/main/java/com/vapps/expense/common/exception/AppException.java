package com.vapps.expense.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends Exception {

	private final int status;

	public AppException() {
		super("Internal Server Error");
		status = HttpStatus.INTERNAL_SERVER_ERROR.value();
	}

	public AppException(int status, String message) {
		super(message);
		this.status = status;
	}

	public AppException(String message) {
		super(message);
		this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
	}
}