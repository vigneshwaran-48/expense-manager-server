package com.vapps.expense.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

	private String id;
	private String name;
	private String firstName;
	private String lastName;
	private int age;
	private String email;
	private String image;

}
