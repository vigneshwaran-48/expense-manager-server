package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.CategoryDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponse extends Response {

	private CategoryDTO category;

	public CategoryResponse(int status, String message, LocalDateTime time, String path, CategoryDTO category) {
		super(status, message, time, path);
		this.category = category;
	}
}
