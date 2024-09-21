package com.vapps.expense.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vapps.expense.common.dto.response.AppErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Autowired
	private ObjectMapper objectMapper;

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		response.setStatus(HttpStatus.FORBIDDEN.value());

		AppErrorResponse appErrorResponse = new AppErrorResponse();
		appErrorResponse.setError(accessDeniedException.getMessage());
		appErrorResponse.setStatus(HttpStatus.FORBIDDEN.value());
		appErrorResponse.setPath(request.getServletPath());
		appErrorResponse.setTime(LocalDateTime.now());

		String responseJSON = "Access Denied!";

		try {
			responseJSON = objectMapper.writeValueAsString(appErrorResponse);
		} catch (JsonProcessingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		response.getWriter().println(responseJSON);
	}
}
