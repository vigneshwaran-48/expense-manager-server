package com.vapps.expense.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FamilySearchDTO {

	private String id;
	private String name;
	private String description;
	private FamilyDTO.Visibility visibility;
	private UserDTO createdBy;
	private LocalDateTime createdTime;
	private String image;
	private FamilyDTO.JoinType joinType;
	private boolean isJoinRequestExists;

}
