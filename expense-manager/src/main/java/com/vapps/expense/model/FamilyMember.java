package com.vapps.expense.model;

import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.dto.FamilyMemberDTO.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
@Data
public class FamilyMember {

    @Id
    private String id;

    @DocumentReference
    private Family family;

    @DocumentReference
    private User member;

    private Role role;

    public FamilyMemberDTO toDTO() {
        FamilyMemberDTO familyMemberDTO = new FamilyMemberDTO();
        familyMemberDTO.setId(id);
        familyMemberDTO.setFamily(family.toDTO());
        familyMemberDTO.setMember(member.toDTO());
        familyMemberDTO.setRole(role);
        return familyMemberDTO;
    }
}
