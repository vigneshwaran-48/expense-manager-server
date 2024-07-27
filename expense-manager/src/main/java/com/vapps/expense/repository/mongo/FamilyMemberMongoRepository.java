package com.vapps.expense.repository.mongo;

import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.model.FamilyMember;
import jakarta.transaction.Transactional;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberMongoRepository extends MongoRepository<FamilyMember, String> {
    List<FamilyMember> findByFamilyId(String familyId);

    Optional<FamilyMember> findByFamilyIdAndMemberId(String familyId, String memberId);

    boolean existsByFamilyIdAndMemberId(String familyId, String memberId);

    List<FamilyMember> findByFamilyIdAndRole(String familyId, FamilyMemberDTO.Role role);

    @Transactional
    void deleteByFamilyIdAndMemberId(String familyId, String memberId);

    Optional<FamilyMember> findByMemberId(String memberId);
}
