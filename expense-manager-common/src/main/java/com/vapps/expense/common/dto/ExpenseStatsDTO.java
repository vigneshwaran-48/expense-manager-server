package com.vapps.expense.common.dto;

import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Data
public class ExpenseStatsDTO {

	public enum ExpenseStatsType {
		PERSONAL,
		FAMILY
	}

	private String id;
	private String ownerId;
	private ExpenseStatsType type;
	private List<ExpenseDTO> recentExpenses;
	private Map<DayOfWeek, Long> weekAmount;
	private Map<String, Long> categoryAmount;
	private Map<String, Long> userAmount;

}
