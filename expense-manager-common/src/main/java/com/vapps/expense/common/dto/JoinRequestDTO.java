package com.vapps.expense.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JoinRequestDTO {

    private String id;
    private UserDTO requestUser;
    private FamilyDTO family;
    private LocalDateTime requestedTime;

}
