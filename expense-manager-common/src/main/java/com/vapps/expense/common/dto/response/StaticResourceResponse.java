package com.vapps.expense.common.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StaticResourceResponse extends Response {

	private String resourceId;

	public StaticResourceResponse(int status, String message, LocalDateTime time, String path, String resourceId) {
		super(status, message, time, path);
		this.resourceId = resourceId;
	}
}
