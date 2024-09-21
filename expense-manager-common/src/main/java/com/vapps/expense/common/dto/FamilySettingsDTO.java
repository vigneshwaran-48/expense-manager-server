package com.vapps.expense.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class FamilySettingsDTO {

	private String id;

	private FamilyDTO family;

	private List<FamilyMemberDTO.Role> inviteAcceptRequestRoles;

	private List<FamilyMemberDTO.Role> familyExpenseRoles;

	private List<FamilyMemberDTO.Role> removeMemberRoles;

	private List<FamilyMemberDTO.Role> updateFamilyRoles;

	private List<FamilyMemberDTO.Role> categoryRoles = List.of(FamilyMemberDTO.Role.LEADER,
			FamilyMemberDTO.Role.MAINTAINER);
}
