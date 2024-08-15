package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.ExpenseDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpenseResponse extends Response {

    private ExpenseDTO expense;

    public ExpenseResponse(int status, String message, LocalDateTime time, String path, ExpenseDTO expense) {
        super(status, message, time, path);
        this.expense = expense;
    }
}
