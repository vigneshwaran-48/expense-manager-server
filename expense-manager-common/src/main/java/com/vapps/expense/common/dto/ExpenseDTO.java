package com.vapps.expense.common.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExpenseDTO {

	public enum ExpenseType {
		PERSONAL, FAMILY
	}

	private String id;
	private String name;
	private String description;
	private FamilyDTO family;
	private UserDTO createdBy;
	private long amount;
	private String currency;
	private String ownerId;
	private ExpenseType type;
	private LocalDateTime time;
	private List<String> invoices;
	private CategoryDTO category;
}
