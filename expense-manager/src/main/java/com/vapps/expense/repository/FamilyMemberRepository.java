package com.vapps.expense.repository;

import com.vapps.expense.model.FamilyMember;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository {

    FamilyMember save(FamilyMember familyMember);

    FamilyMember update(FamilyMember familyMember);

    List<FamilyMember> findByFamilyId(String familyId);

    Optional<FamilyMember> findByMemberId(String memberId);

}
