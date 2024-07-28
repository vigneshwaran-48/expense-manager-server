package com.vapps.expense.common.dto;

import lombok.Data;

import java.util.Map;

@Data
public class InvitationDTO {

    public enum Type {
        FAMILY_INVITE
    }

    private String id;
    private String title;
    private String content;
    private Map<String, Object> properties;
    private UserDTO recipient;
    private UserDTO from;
    private Type type;

}
