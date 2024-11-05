package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.exception.AppException;

import java.util.List;
import java.util.Optional;

public interface FamilyService {

	FamilyDTO createFamily(String userId, FamilyDTO family) throws AppException;

	FamilyDTO updateFamily(String userId, String familyId, FamilyDTO family) throws AppException;

	Optional<FamilyDTO> getFamilyById(String userId, String id) throws AppException;

	void deleteFamilyById(String userId, String id) throws AppException;

	void addMember(String userId, String familyId, String memberId, FamilyMemberDTO.Role role) throws AppException;

	void removeMember(String userId, String familyId, String memberId) throws AppException;

	void updateRole(String userId, String familyId, String memberId, FamilyMemberDTO.Role role) throws AppException;

	InvitationDTO inviteMember(String userId, String familyId, String memberId, FamilyMemberDTO.Role role)
			throws AppException;

	Optional<FamilyDTO> getUserFamily(String userId);

	SearchDTO<FamilySearchDTO> searchFamily(String userId, String query, int page) throws AppException;

	FamilyMemberDTO.Role getUserRoleInFamily(String userId, String familyId) throws AppException;

	List<FamilyMemberDTO> getFamilyMembers(String userId, String familyId) throws AppException;

	Optional<FamilyMemberDTO> getFamilyMember(String userId, String familyId, String memberId) throws AppException;

	JoinRequestDTO joinRequestFamily(String userId, String familyId) throws AppException;

	void acceptJoinRequest(String userId, String requestId) throws AppException;

	void rejectJoinRequest(String userId, String requestId) throws AppException;

	List<JoinRequestDTO> getFamilyJoinRequests(String userId, String familyId) throws AppException;

	List<InvitationDTO> getAllInvitationsOfFamily(String userId, String familyId) throws AppException;

	List<UserDTO> getNonFamilyAndNonInvitedUsers(String userId) throws AppException;

	FamilySettingsDTO getFamilySettings(String userId, String familyId) throws AppException;

	void updateFamilySettings(String userId, String familyId, FamilySettingsDTO settings) throws AppException;
}
