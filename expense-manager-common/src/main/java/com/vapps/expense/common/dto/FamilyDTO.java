package com.vapps.expense.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FamilyDTO {

    public enum Visibility {
        PUBLIC, PRIVATE
    }

    private String id;
    private String name;
    private String description;
    private Visibility visibility;
    private UserDTO createdBy;
    private LocalDateTime createdTime;
    private String image;

}
