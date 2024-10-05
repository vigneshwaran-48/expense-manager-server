package com.vapps.expense.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class ExpenseStatsDTO {

	public enum ExpenseStatsType {
		PERSONAL,
		FAMILY
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CategoryAmount {
		private long amount;
		private CategoryDTO category;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UserAmount {
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
