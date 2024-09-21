package com.vapps.expense.common.dto;

import lombok.Data;

@Data
public class CategoryDTO {

	public enum CategoryType {
		PERSONAL,
		FAMILY
	}

	private String id;
	private String name;
	private String description;
	private String image;
	private UserDTO createdBy;
	private String ownerId;
	private CategoryType type;
}
