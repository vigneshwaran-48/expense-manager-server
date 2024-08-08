package com.vapps.expense.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO<T> {

    private List<T> results;
    private int currentPage;
    private int nextPage;
    private int totalPages;

}
