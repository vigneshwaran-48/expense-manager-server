package com.vapps.expense.controller;

import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.dto.response.*;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.ExpenseStatsService;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.util.Endpoints;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(Endpoints.FAMILY_API)
public class FamilyController {

	@Autowired
	private FamilyService familyService;

	@Autowired
	private ExpenseStatsService statsService;

	@PostMapping
	public ResponseEntity<FamilyResponse> createFamily(@RequestBody FamilyDTO family, Principal principal,
			HttpServletRequest request) throws AppException {

		String userId = principal.getName();
		FamilyDTO createdFamily = familyService.createFamily(userId, family);

		return ResponseEntity.ok(new FamilyResponse(HttpStatus.OK.value(), "Created Family!", LocalDateTime.now(),
				request.getServletPath(), createdFamily));
	}

	@PostMapping(Endpoints.INVITE_MEMBER_PATH)
	public ResponseEntity<InvitationResponse> inviteMember(@PathVariable String familyId, @PathVariable String memberId,
			@RequestParam FamilyMemberDTO.Role role, Principal principal, HttpServletRequest request)
			throws AppException {
		String userId = principal.getName();
		InvitationDTO invitation = familyService.inviteMember(userId, familyId, memberId, role);
		return ResponseEntity.ok(
				new InvitationResponse(HttpStatus.OK.value(), "Invited member!", LocalDateTime.now(),
						request.getServletPath(), invitation));
	}

	@DeleteMapping(Endpoints.REMOVE_MEMBER_FROM_FAMILY_PATH)
	public ResponseEntity<Response> removeMember(@PathVariable String familyId, @PathVariable String memberId,
			Principal principal, HttpServletRequest request) throws AppException {
		String userId = principal.getName();

		familyService.removeMember(userId, familyId, memberId);
		return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Removed member from family!", LocalDateTime.now(),
				request.getServletPath()));
	}

	@GetMapping(Endpoints.GET_FAMILY_PATH)
	public ResponseEntity<FamilyResponse> getFamily(@PathVariable String familyId, Principal principal,
			HttpServletRequest request) throws AppException {

		String userId = principal.getName();
		Optional<FamilyDTO> familyDTO = familyService.getFamilyById(userId, familyId);
		if (familyDTO.isEmpty()) {
			throw new AppException(HttpStatus.NOT_FOUND.value(), "Family not found!");
		}
		return ResponseEntity.ok(
				new FamilyResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
						familyDTO.get()));
	}

	@PatchMapping(Endpoints.UPDATE_FAMILY_PATH)
	public ResponseEntity<FamilyResponse> updateFamily(@PathVariable String familyId, @RequestBody FamilyDTO familyDTO,
			Principal principal, HttpServletRequest request) throws AppException {

		String userId = principal.getName();
		FamilyDTO updatedFamily = familyService.updateFamily(userId, familyId, familyDTO);
		return ResponseEntity.ok(
				new FamilyResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
						updatedFamily));
	}

	@DeleteMapping(Endpoints.DELETE_FAMILY_PATH)
	public ResponseEntity<Response> deleteFamily(@PathVariable String familyId, Principal principal,
			HttpServletRequest request) throws AppException {

		String userId = principal.getName();
		familyService.deleteFamilyById(userId, familyId);
		return ResponseEntity.ok(
				new Response(HttpStatus.OK.value(), "Deleted family!", LocalDateTime.now(), request.getServletPath()));
	}

	@GetMapping(Endpoints.GET_USER_FAMILY_PATH)
	public ResponseEntity<FamilyResponse> getUserFamily(Principal principal, HttpServletRequest request)
			throws AppException {

		String userId = principal.getName();
		Optional<FamilyDTO> family = familyService.getUserFamily(userId);
		return ResponseEntity.ok(
				new FamilyResponse(family.isPresent() ? HttpStatus.OK.value() : HttpStatus.NO_CONTENT.value(),
						"success", LocalDateTime.now(), request.getServletPath(),
						family.isPresent() ? family.get() : null));
	}

	@GetMapping(Endpoints.SEARCH_FAMILY_PATH)
	public ResponseEntity<SearchResponse<FamilySearchDTO>> searchFamily(@RequestParam String query,
			@RequestParam int page,
			Principal principal, HttpServletRequest request) throws AppException {

		String userId = principal.getName();
		SearchDTO<FamilySearchDTO> results = familyService.searchFamily(userId, query, page);
		return ResponseEntity.ok(
				new SearchResponse<>(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
						results));
	}

	@GetMapping(Endpoints.GET_FAMILY_MEMBERS_PATH)
	public ResponseEntity<FamilyMembersResponse> getFamilyMembers(@PathVariable String familyId, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		List<FamilyMemberDTO> members = familyService.getFamilyMembers(userId, familyId);
		return ResponseEntity.ok(new FamilyMembersResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(),
				request.getServletPath(), members));
	}

	@PostMapping(Endpoints.UPDATE_FAMILY_MEMBER_ROLE_PATH)
	public ResponseEntity<Response> updateFamilyMemberRole(@PathVariable String familyId, @PathVariable String memberId,
			@RequestParam FamilyMemberDTO.Role role, Principal principal, HttpServletRequest request)
			throws AppException {
		String userId = principal.getName();
		familyService.updateRole(userId, familyId, memberId, role);

		return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Updated Member Role!", LocalDateTime.now(),
				request.getServletPath()));
	}

	@GetMapping(Endpoints.GET_FAMILY_MEMBER_PATH)
	public ResponseEntity<FamilyMemberResponse> getFamilyMember(@PathVariable String familyId,
			@PathVariable String memberId, Principal principal, HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		Optional<FamilyMemberDTO> member = familyService.getFamilyMember(userId, familyId, memberId);
		if (member.isEmpty()) {
			throw new AppException(HttpStatus.BAD_REQUEST.value(), "Member not present in family!");
		}
		return ResponseEntity.ok(new FamilyMemberResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(),
				request.getServletPath(), member.get()));
	}

	@PostMapping(Endpoints.FAMILY_JOIN_REQUEST_PATH)
	public ResponseEntity<JoinRequestResponse> joinRequest(@PathVariable String familyId, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		JoinRequestDTO requestDTO = familyService.joinRequestFamily(userId, familyId);
		return ResponseEntity.ok(new JoinRequestResponse(HttpStatus.OK.value(), "Join request sent",
				LocalDateTime.now(), request.getServletPath(), requestDTO));
	}

	@PostMapping(Endpoints.FAMILY_ACCEPT_JOIN_REQUEST_PATH)
	public ResponseEntity<Response> acceptJoinRequest(@PathVariable String requestId, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		familyService.acceptJoinRequest(userId, requestId);

		return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Accepted the request!", LocalDateTime.now(),
				request.getServletPath()));
	}

	@PostMapping(Endpoints.FAMILY_REJECT_JOIN_REQUEST_PATH)
	public ResponseEntity<Response> rejectJoinRequest(@PathVariable String requestId, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		familyService.rejectJoinRequest(userId, requestId);

		return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Rejected the request!", LocalDateTime.now(),
				request.getServletPath()));
	}

	@GetMapping(Endpoints.FAMILY_JOIN_REQUEST_PATH)
	public ResponseEntity<JoinRequestsResponse> listJoinRequests(@PathVariable String familyId, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		List<JoinRequestDTO> requests = familyService.getFamilyJoinRequests(userId, familyId);

		return ResponseEntity.ok(new JoinRequestsResponse(HttpStatus.OK.value(), "success",
				LocalDateTime.now(), request.getServletPath(), requests));
	}

	@GetMapping(Endpoints.GET_USERS_FAMILY_ROLE_PATH)
	public ResponseEntity<FamilyRoleResponse> getUsersFamilyRole(@PathVariable String familyId, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		FamilyMemberDTO.Role role = familyService.getUserRoleInFamily(userId, familyId);
		return ResponseEntity.ok(new FamilyRoleResponse(HttpStatus.OK.value(), "success",
				LocalDateTime.now(), request.getServletPath(), role));
	}

	@GetMapping(Endpoints.GET_FAMILY_INVITATIONS_PATH)
	public ResponseEntity<InvitationsResponse> getFamilyInvitations(@PathVariable String familyId, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		List<InvitationDTO> invitations = familyService.getAllInvitationsOfFamily(userId, familyId);
		return ResponseEntity.ok(
				new InvitationsResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(), request.getServletPath(),
						invitations));
	}

	@GetMapping(Endpoints.GET_FAMILY_SETTINGS_PATH)
	public ResponseEntity<FamilySettingsResponse> getFamilySettings(@PathVariable String familyId, Principal principal,
			HttpServletRequest request) throws AppException {
		String userId = principal.getName();
		FamilySettingsDTO settings = familyService.getFamilySettings(userId, familyId);
		return ResponseEntity.ok(
				new FamilySettingsResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(),
						request.getServletPath(),
						settings));
	}

	@GetMapping(Endpoints.GET_FAMILY_STATS_PATH)
	public ResponseEntity<ExpenseStatsResponse> getFamilyStats(@PathVariable String familyId, Principal principal,
			HttpServletRequest request) throws AppException {

		String userId = principal.getName();
		ExpenseStatsDTO stats = statsService.getStats(userId, familyId, ExpenseStatsDTO.ExpenseStatsType.FAMILY).get();

		return ResponseEntity.ok(
				new ExpenseStatsResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(),
						request.getServletPath(),
						stats));
	}
}
