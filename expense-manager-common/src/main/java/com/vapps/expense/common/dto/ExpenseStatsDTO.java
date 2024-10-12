package com.vapps.expense.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
	private Map<DayOfWeek, Long> weekAmount;

}
