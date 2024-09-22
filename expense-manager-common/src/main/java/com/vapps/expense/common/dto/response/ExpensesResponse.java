package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.ExpenseDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExpensesResponse extends Response {

	private List<ExpenseDTO> expenses;

	public ExpensesResponse(int status, String message, LocalDateTime time, String path, List<ExpenseDTO> expenses) {
		super(status, message, time, path);
		this.expenses = expenses;
	}

}
