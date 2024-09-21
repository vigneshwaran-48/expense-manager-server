package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.CategoryDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoriesResponse extends Response {

	private List<CategoryDTO> categories;

	public CategoriesResponse(int status, String message, LocalDateTime time, String path,
			List<CategoryDTO> categories) {
		super(status, message, time, path);
		this.categories = categories;
	}

}
