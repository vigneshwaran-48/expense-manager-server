package com.vapps.expense.common.dto;

import java.time.LocalDateTime;

public class ExpenseFilter {

    public enum SearchBy {
        NAME,
        DESCRIPTION,
        CATEGORY,
        OWNER,
        ALL
    }

    private boolean isPersonal;
    private LocalDateTime time;
    private LocalDateTime start;
    private LocalDateTime end;
    private String query;
    private SearchBy searchBy;
}
