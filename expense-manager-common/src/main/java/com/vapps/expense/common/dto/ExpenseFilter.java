package com.vapps.expense.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpenseFilter {

	public enum SearchBy {
		NAME,
		DESCRIPTION,
		CATEGORY,
		OWNER,
		ALL
	}

	private boolean isFamily;
	private LocalDateTime start;
	private LocalDateTime end;
	private String query;
	private SearchBy searchBy;
}
