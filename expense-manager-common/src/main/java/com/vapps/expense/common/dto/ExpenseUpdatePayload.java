package com.vapps.expense.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpenseUpdatePayload {

    private String name;
    private String description;
    private long amount;
    private String currency;
    private LocalDateTime time;
}
