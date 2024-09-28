package com.vapps.expense.model;

import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.dto.FamilySettingsDTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@Document
public class FamilySettings {

	private String id;

	@DocumentReference
	private Family family;

	// Roles
	private List<FamilyMemberDTO.Role> inviteAcceptRequestRoles = List.of(FamilyMemberDTO.Role.LEADER);

	private List<FamilyMemberDTO.Role> familyExpenseRoles = List.of(FamilyMemberDTO.Role.LEADER,
			FamilyMemberDTO.Role.MAINTAINER);

	private List<FamilyMemberDTO.Role> removeMemberRoles = List.of(FamilyMemberDTO.Role.LEADER);

	private List<FamilyMemberDTO.Role> updateFamilyRoles = List.of(FamilyMemberDTO.Role.LEADER);

	private List<FamilyMemberDTO.Role> categoryRoles = List.of(FamilyMemberDTO.Role.LEADER,
			FamilyMemberDTO.Role.MAINTAINER);

	public FamilySettingsDTO toDTO() {
		FamilySettingsDTO familySettingsDTO = new FamilySettingsDTO();
		familySettingsDTO.setId(id);
		familySettingsDTO.setFamily(family.toDTO());
		familySettingsDTO.setFamilyExpenseRoles(familyExpenseRoles);
		familySettingsDTO.setRemoveMemberRoles(removeMemberRoles);
		familySettingsDTO.setInviteAcceptRequestRoles(inviteAcceptRequestRoles);
		familySettingsDTO.setUpdateFamilyRoles(updateFamilyRoles);
		return familySettingsDTO;
	}
}
