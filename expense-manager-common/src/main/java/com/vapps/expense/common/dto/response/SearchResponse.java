package com.vapps.expense.common.dto.response;

import com.vapps.expense.common.dto.SearchDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse<T> extends Response {

	private SearchDTO<T> result;

	public SearchResponse(int status, String message, LocalDateTime time, String path, SearchDTO<T> result) {
		super(status, message, time, path);
		this.result = result;
	}
}
