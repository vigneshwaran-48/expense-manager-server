package com.vapps.expense.service;

import com.vapps.expense.annotation.FamilyIdValidator;
import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.*;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.service.InvitationService;
import com.vapps.expense.common.service.UserService;
import com.vapps.expense.model.Family;
import com.vapps.expense.model.FamilyMember;
import com.vapps.expense.model.User;
import com.vapps.expense.repository.FamilyMemberRepository;
import com.vapps.expense.repository.FamilyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FamilyServiceImpl implements FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private InvitationService invitationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(FamilyServiceImpl.class);
    private static final int PAGE_SIZE = 10;

    @Override
    @UserIdValidator(positions = 0)
    public FamilyDTO createFamily(String userId, FamilyDTO family) throws AppException {
        if (familyMemberRepository.findByMemberId(userId).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Already present in a family!");
        }
        UserDTO userDTO = userService.getUser(userId).get();
        family.setCreatedBy(userDTO);
        Family familyModel = Family.build(family);
        familyModel.setCreatedTime(LocalDateTime.now());
        Family savedFamily = familyRepository.save(familyModel);
        if (savedFamily == null) {
            LOGGER.error("Created family is null!");
            throw new AppException("Error while creating family");
        }
        LOGGER.info("Created Family {}", savedFamily.getId());

        FamilyMember familyMember = new FamilyMember();
        familyMember.setFamily(savedFamily);
        familyMember.setMember(User.build(userDTO));
        familyMember.setRole(FamilyMemberDTO.Role.LEADER);
        familyMemberRepository.save(familyMember);

        LOGGER.info("Added user {} as family {}'s leader", userId, savedFamily.getId());
        return savedFamily.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    @FamilyIdValidator(userIdPosition = 0, positions = 1)
    public FamilyDTO updateFamily(String userId, String familyId, FamilyDTO familyDTO) throws AppException {
        Optional<FamilyMember> userMember = familyMemberRepository.findByFamilyIdAndMemberId(familyId, userId);
        if (userMember.isEmpty() || (userMember.get().getRole() != FamilyMemberDTO.Role.LEADER && userMember.get()
                .getRole() != FamilyMemberDTO.Role.MAINTAINER)) {
            throw new AppException(HttpStatus.FORBIDDEN.value(), "You are not allowed to update this family!");
        }
        Family existingFamily = familyRepository.findById(familyId).get();
        familyDTO.setCreatedBy(existingFamily.getCreatedBy().toDTO());

        Family familyToUpdate = Family.build(familyDTO);
        familyToUpdate.setId(familyId);
        familyToUpdate.setCreatedTime(existingFamily.getCreatedTime());

        if (familyToUpdate.getName() == null) {
            familyToUpdate.setName(existingFamily.getName());
        }
        if (familyToUpdate.getDescription() == null) {
            familyToUpdate.setDescription(existingFamily.getDescription());
        }
        if (familyToUpdate.getImage() == null) {
            familyToUpdate.setImage(existingFamily.getImage());
        }
        if (familyToUpdate.getVisibility() == null) {
            familyToUpdate.setVisibility(existingFamily.getVisibility());
        }
        if (familyToUpdate.getJoinType() == null) {
            familyToUpdate.setJoinType(existingFamily.getJoinType());
        }
        Family updatedFamily = familyRepository.update(familyToUpdate);
        if (updatedFamily == null) {
            throw new AppException("Error while updating the family!");
        }
        return updatedFamily.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    public Optional<FamilyDTO> getFamilyById(String userId, String id) throws AppException {
        Optional<Family> family = familyRepository.findById(id);
        if (family.isEmpty()) {
            return Optional.empty();
        }
        if (family.get()
                .getVisibility() == FamilyDTO.Visibility.PUBLIC || familyMemberRepository.existsByFamilyIdAndMemberId(
                id, userId)) {
            return Optional.of(family.get().toDTO());
        }
        return Optional.empty();
    }

    @Override
    @UserIdValidator(positions = 0)
    @FamilyIdValidator(userIdPosition = 0, positions = 1)
    public void deleteFamilyById(String userId, String id) throws AppException {
        Optional<FamilyMember> userMember = familyMemberRepository.findByFamilyIdAndMemberId(id, userId);
        if (userMember.isEmpty() || userMember.get().getRole() != FamilyMemberDTO.Role.LEADER) {
            throw new AppException(HttpStatus.FORBIDDEN.value(), "You are not allowed to delete this family!");
        }
        familyMemberRepository.deleteByFamilyId(id);
        LOGGER.info("Deleted all members relation in the family {}", id);
        familyRepository.deleteById(id);
        LOGGER.info("Deleted family {}", id);
    }

    @Override
    @UserIdValidator(positions = { 0, 2 })
    @FamilyIdValidator(userIdPosition = 0, positions = 1)
    public void addMember(String userId, String familyId, String memberId, FamilyMemberDTO.Role role)
            throws AppException {
        UserDTO member = userService.getUser(memberId).get();
        Family family = familyRepository.findById(familyId).get();

        Optional<FamilyMember> userMember = familyMemberRepository.findByFamilyIdAndMemberId(familyId, userId);
        if (userMember.isEmpty() || userMember.get().getRole() != FamilyMemberDTO.Role.LEADER) {
            throw new AppException(HttpStatus.FORBIDDEN.value(), "You are not allowed add member to this family");
        }

        if (familyMemberRepository.existsByFamilyIdAndMemberId(familyId, memberId)) {
            throw new AppException(memberId + " is already a member of family " + familyId);
        }
        if (role == FamilyMemberDTO.Role.LEADER) {
            userMember.get().setRole(FamilyMemberDTO.Role.MAINTAINER);
            familyMemberRepository.updateRole(userMember.get());
        }

        FamilyMember newMember = new FamilyMember();
        newMember.setRole(role);
        newMember.setFamily(family);
        newMember.setMember(User.build(member));
        FamilyMember addedFamilyMember = familyMemberRepository.save(newMember);

        if (addedFamilyMember == null) {
            throw new AppException("Error while adding member to the family!");
        }
    }

    @Override
    @UserIdValidator(positions = { 0, 2 })
    @FamilyIdValidator(userIdPosition = 0, positions = 1)
    public void removeMember(String userId, String familyId, String memberId) throws AppException {
        Optional<FamilyMember> userMember = familyMemberRepository.findByFamilyIdAndMemberId(familyId, userId);
        if (userMember.isEmpty() || userMember.get().getRole() != FamilyMemberDTO.Role.LEADER) {
            throw new AppException(HttpStatus.FORBIDDEN.value(), "You are not allowed add member to this family");
        }
        if (userId.equals(memberId)) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(),
                    "Promote anyone as leader before exiting the family!");
        }
        familyMemberRepository.deleteByFamilyIdAndMemberId(familyId, memberId);
    }

    @Override
    @UserIdValidator(positions = { 0, 2 })
    @FamilyIdValidator(userIdPosition = 0, positions = 1)
    public void updateRole(String userId, String familyId, String memberId, FamilyMemberDTO.Role role)
            throws AppException {
        Optional<FamilyMember> userMember = familyMemberRepository.findByMemberId(userId);

        if (userMember.isEmpty() || userMember.get().getRole() != FamilyMemberDTO.Role.LEADER) {
            throw new AppException(HttpStatus.FORBIDDEN.value(), "You are not allowed add member to this family");
        }
        if (role == FamilyMemberDTO.Role.LEADER) {
            userMember.get().setRole(FamilyMemberDTO.Role.MAINTAINER);
            familyMemberRepository.updateRole(userMember.get());
        }

        Optional<FamilyMember> memberToUpdate = familyMemberRepository.findByFamilyIdAndMemberId(familyId, memberId);
        if (memberToUpdate.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(), "Member not exists in the family");
        }

        memberToUpdate.get().setRole(role);
        FamilyMember updatedFamilyMember = familyMemberRepository.updateRole(memberToUpdate.get());

        if (updatedFamilyMember == null) {
            throw new AppException("Error while update member's role!");
        }
    }

    @Override
    @UserIdValidator(positions = { 0, 2 })
    @FamilyIdValidator(userIdPosition = 0, positions = 1)
    public void inviteMember(String userId, String familyId, String memberId, FamilyMemberDTO.Role role)
            throws AppException {
        Optional<FamilyMember> userMember = familyMemberRepository.findByMemberId(userId);

        if (userMember.isEmpty() || userMember.get().getRole() != FamilyMemberDTO.Role.LEADER) {
            throw new AppException(HttpStatus.FORBIDDEN.value(), "You are not allowed invite member to this family!");
        }

        Map<String, Object> invitationProps = new HashMap<>();
        invitationProps.put(InvitationDTO.InvitationProps.FAMILY_ID, familyId);
        invitationProps.put(InvitationDTO.InvitationProps.ROLE, role);

        UserDTO from = userService.getUser(userId).get();
        UserDTO member = userService.getUser(memberId).get();

        InvitationDTO invitationDTO = new InvitationDTO();
        invitationDTO.setType(InvitationDTO.Type.FAMILY_INVITE);
        invitationDTO.setFrom(from);
        invitationDTO.setTitle("You got invitation to join a family");
        invitationDTO.setRecipient(member);
        invitationDTO.setProperties(invitationProps);
        invitationDTO.setContent("Hi " + member.getName() + ", " + from.getName() + " invites you to his family");

        FamilyDTO familyDTO = getFamilyById(userId, familyId).get();
        Context context = new Context();
        context.setVariable("familyDescription", familyDTO.getDescription());
        context.setVariable("familyName", familyDTO.getName());

        invitationService.sendInvitation(userId, invitationDTO, context);
    }

    @Override
    @UserIdValidator(positions = 0)
    public Optional<FamilyDTO> getUserFamily(String userId) {
        Optional<FamilyMember> familyMember = familyMemberRepository.findByMemberId(userId);
        if (familyMember.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(familyMember.get().getFamily().toDTO());
    }

    @Override
    public SearchDTO<FamilyDTO> searchFamily(String userId, String query, int page) throws AppException {

        int queryPage = page - 1;

        SearchDTO<FamilyDTO> familyResults = new SearchDTO<>();
        familyResults.setResults(new ArrayList<>());
        familyResults.setCurrentPage(page);
        familyResults.setNextPage(-1);

        familyRepository.findByIdOrNameContainingIgnoreCaseAndVisibility(query, query, FamilyDTO.Visibility.PUBLIC,
                        PageRequest.of(queryPage, PAGE_SIZE + 1)).stream()
                .filter(family -> !familyMemberRepository.existsByFamilyIdAndMemberId(family.getId(), userId))
                .forEach(family -> familyResults.getResults().add(family.toDTO()));

        if (familyResults.getResults().size() > PAGE_SIZE) {
            familyResults.getResults().remove(familyResults.getResults().size() - 1);
            familyResults.setNextPage(page + 1);
        }
        int resultSize = familyRepository.findByIdOrNameContainingIgnoreCaseAndVisibility(query, query,
                FamilyDTO.Visibility.PUBLIC).size();
        familyResults.setTotalPages(resultSize >= PAGE_SIZE ? resultSize % PAGE_SIZE : 1);
        return familyResults;
    }
}
