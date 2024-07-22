package com.vapps.expense.model;

import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.dto.FamilyMemberDTO.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class FamilyMember {

    @Id
    private String id;

    private Family family;
    private User member;
    private Role role;

}
