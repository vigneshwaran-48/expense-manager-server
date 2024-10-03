package com.vapps.expense.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@Document
public class ExpenseStats {

	@Id
	private String id;

	@DocumentReference
	private Family family;

	private User user;
}
