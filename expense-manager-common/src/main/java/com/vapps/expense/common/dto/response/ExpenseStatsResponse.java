package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.ExpenseStatsDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpenseStatsResponse extends Response {

	private ExpenseStatsDTO stats;

	public ExpenseStatsResponse(int status, String message, LocalDateTime time, String path, ExpenseStatsDTO stats) {
		this.stats = stats;
	}
}
