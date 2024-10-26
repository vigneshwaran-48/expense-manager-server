package com.vapps.expense.model;

import com.vapps.expense.common.dto.ExpenseDTO;
import com.vapps.expense.common.dto.ExpenseStatsDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Document
public class ExpenseStats {

	@Id
	private String id;

	private String ownerId;
	private ExpenseStatsDTO.ExpenseStatsType type;
	private long currentWeekTotal;
	private long currentMonthTotal;
	private List<Expense> recentExpenses;
	private Map<DayOfWeek, Long> weekAmount;
	private Map<String, Long> categoryAmount;
	private Map<String, Long> userAmount;

	public ExpenseStatsDTO toDTO() {
		ExpenseStatsDTO stats = new ExpenseStatsDTO();
		stats.setId(id);
		stats.setRecentExpenses(
				recentExpenses.stream().map(Expense::toDTO).sorted(Comparator.comparing(ExpenseDTO::getTime))
						.collect(Collectors.toList()));
		stats.setOwnerId(ownerId);
		stats.setType(type);
		stats.setWeekAmount(weekAmount);
		stats.setCategoryAmount(categoryAmount);
		stats.setUserAmount(userAmount);
		return stats;
	}

	public static ExpenseStats build(ExpenseStatsDTO expenseStatsDTO) {
		ExpenseStats expenseStats = new ExpenseStats();
		expenseStats.setId(expenseStatsDTO.getId());
		expenseStats.setRecentExpenses(
				expenseStatsDTO.getRecentExpenses().stream().map(Expense::build).collect(Collectors.toList()));
		expenseStats.setType(expenseStatsDTO.getType());
		expenseStats.setOwnerId(expenseStatsDTO.getOwnerId());
		expenseStats.setWeekAmount(expenseStatsDTO.getWeekAmount());
		expenseStats.setCategoryAmount(expenseStatsDTO.getCategoryAmount());
		expenseStats.setUserAmount(expenseStatsDTO.getUserAmount());
		return expenseStats;
	}
}
