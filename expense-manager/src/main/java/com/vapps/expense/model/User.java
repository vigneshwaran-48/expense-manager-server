package com.vapps.expense.model;

import com.vapps.expense.common.dto.UserDTO;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class User {

	@Id
	private String id;

	private String name;
	private String firstName;
	private String lastName;
	private int age;
	private String email;
	private String image;

	public UserDTO toDTO() {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(id);
		userDTO.setAge(age);
		userDTO.setName(name);
		userDTO.setFirstName(firstName);
		userDTO.setLastName(lastName);
		userDTO.setEmail(email);
		userDTO.setImage(image);
		return userDTO;
	}

	public static User build(UserDTO userDTO) {
		User user = new User();
		user.setId(userDTO.getId());
		user.setEmail(userDTO.getEmail());
		user.setName(userDTO.getName());
		user.setAge(userDTO.getAge());
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setImage(userDTO.getImage());
		return user;
	}

}
