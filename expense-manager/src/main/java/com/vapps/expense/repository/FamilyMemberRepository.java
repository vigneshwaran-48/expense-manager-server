package com.vapps.expense.repository;

import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.model.FamilyMember;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository {

    FamilyMember save(FamilyMember familyMember);

    FamilyMember update(FamilyMember familyMember);

    List<FamilyMember> findByFamilyId(String familyId);

    Optional<FamilyMember> findByMemberId(String memberId);

    Optional<FamilyMember> findByFamilyIdAndMemberId(String familyId, String memberId);

    boolean existsByFamilyIdAndMemberId(String familyId, String memberId);

    FamilyMember updateRole(FamilyMember familyMember);

    List<FamilyMember> findByFamilyIdAndRole(String familyId, FamilyMemberDTO.Role role);

    @Transactional
    void deleteByFamilyIdAndMemberId(String familyId, String memberId);

    @Transactional
    void deleteByFamilyId(String familyId);

}
