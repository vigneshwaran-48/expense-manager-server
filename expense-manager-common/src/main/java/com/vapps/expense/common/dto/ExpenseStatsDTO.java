package com.vapps.expense.common.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExpenseStatsDTO {

	public enum ExpenseStatsType {
		PERSONAL,
		FAMILY
	}

	public class CategoryAmount {
		private long amount;
		private CategoryDTO category;
	}

	public class UserAmount {
		private long amount;
		private UserDTO user;
	}

	private String id;
	private String ownerId;
	private ExpenseStatsType type;
	private long currentWeekTotal;
	private long currentMonthTotal;
	private List<ExpenseDTO> recentExpenses;
	private List<CategoryAmount> topCategories;
	private List<UserAmount> topUsers;

}
