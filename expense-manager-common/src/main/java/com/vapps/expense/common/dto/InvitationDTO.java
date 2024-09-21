package com.vapps.expense.common.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class InvitationDTO {

	public static class InvitationProps {
		public static final String FAMILY_ID = "FAMILY_ID";
		public static final String ROLE = "ROLE";
	}

	public enum Type {
		FAMILY_INVITE
	}

	private String id;
	private String title;
	private String content;
	private Map<String, Object> properties;
	private UserDTO recipient;
	private UserDTO from;
	private Type type;
	private LocalDateTime sentTime;

}
