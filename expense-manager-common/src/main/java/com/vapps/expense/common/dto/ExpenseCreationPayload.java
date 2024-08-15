package com.vapps.expense.common.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExpenseCreationPayload {

    private String name;
    private String description;
    private String familyId;
    private long amount;
    private String currency;
    private ExpenseDTO.ExpenseType type;
    private LocalDateTime time;

}
