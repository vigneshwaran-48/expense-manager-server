package com.vapps.expense.common.dto;

import lombok.Data;

@Data
public class FamilyMemberDTO {

	public enum Role {
		LEADER,
		MAINTAINER,
		MEMBER
	}

	private String id;
	private FamilyDTO family;
	private UserDTO member;
	private Role role;
}
