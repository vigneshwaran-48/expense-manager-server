package com.vapps.expense.model;

import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.dto.ExpenseStatsDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Document
public class ExpenseStats {

	@Data
	public class CategoryAmount {
		private Category category;
		private long amount;
	}

	@Data
	public class UserAmount {
		private User user;
		private long amount;
	}

	@Id
	private String id;

	private String ownerId;
	private ExpenseStatsDTO.ExpenseStatsType type;
	private long currentWeekTotal;
	private long currentMonthTotal;
	private List<Expense> recentExpenses;
	private List<CategoryAmount> topCategories;
	private List<UserAmount> topUsers;
	private Map<LocalDate, Long> amountSpentPerDay;

	public ExpenseStatsDTO toDTO() {
		ExpenseStatsDTO stats = new ExpenseStatsDTO();
		stats.setId(id);
		stats.setRecentExpenses(recentExpenses.stream().map(Expense::toDTO).sorted(
				Comparator.comparing(ExpenseDTO::getTime)).collect(Collectors.toList()));
		stats.setOwnerId(ownerId);
		stats.setType(type);
		stats.setTopCategories(topCategories.stream()
				.map(cat -> new ExpenseStatsDTO.CategoryAmount(cat.getAmount(), cat.getCategory().toDTO()))
				.collect(Collectors.toList()));
		stats.setCurrentMonthTotal(currentMonthTotal);
		stats.setCurrentWeekTotal(currentWeekTotal);
		stats.setTopUsers(
				topUsers.stream().map(user -> new ExpenseStatsDTO.UserAmount(user.getAmount(), user.getUser().toDTO()))
						.collect(Collectors.toList()));
		stats.setAmountSpentPerDay(amountSpentPerDay);
		return stats;
	}
}
