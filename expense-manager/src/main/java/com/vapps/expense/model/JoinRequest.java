package com.vapps.expense.model;

import com.vapps.expense.common.dto.JoinRequestDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class JoinRequest {

	private String id;
	private User requestUser;
	private Family family;
	private LocalDateTime requestedTime;

	public JoinRequestDTO toDTO() {
		JoinRequestDTO joinRequestDTO = new JoinRequestDTO();
		joinRequestDTO.setId(id);
		joinRequestDTO.setRequestedTime(requestedTime);
		joinRequestDTO.setFamily(family.toDTO());
		joinRequestDTO.setRequestUser(requestUser.toDTO());
		return joinRequestDTO;
	}
}
