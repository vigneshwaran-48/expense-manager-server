package com.vapps.expense.common.advice;

import com.vapps.expense.common.dto.response.AppErrorResponse;
import com.vapps.expense.common.exception.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestControllerAdviceHandler {

	private static Logger LOGGER = LoggerFactory.getLogger(RestControllerAdviceHandler.class);

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(Exception ex, HttpServletRequest request) {
		LOGGER.error(ex.getMessage(), ex);
		AppErrorResponse response =
				new AppErrorResponse(500, ex.getMessage(), LocalDateTime.now(), request.getServletPath());
		return ResponseEntity.internalServerError().body(response);
	}

	@ExceptionHandler(AppException.class)
	public ResponseEntity<?> handleException(AppException ex, HttpServletRequest request) {
		LOGGER.error(ex.getMessage(), ex);
		AppErrorResponse response =
				new AppErrorResponse(ex.getStatus(), ex.getMessage(), LocalDateTime.now(), request.getServletPath());
		return ResponseEntity.status(ex.getStatus()).body(response);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<?> handleException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
		LOGGER.error(ex.getMessage(), ex);
		AppErrorResponse response =
				new AppErrorResponse(HttpStatus.BAD_REQUEST.value(), "Upload size is larger", LocalDateTime.now(),
						request.getServletPath());
		return ResponseEntity.internalServerError().body(response);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handleException(AccessDeniedException ex, HttpServletRequest request) {
		LOGGER.error(ex.getMessage(), ex);
		AppErrorResponse response =
				new AppErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage(), LocalDateTime.now(),
						request.getServletPath());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

}
