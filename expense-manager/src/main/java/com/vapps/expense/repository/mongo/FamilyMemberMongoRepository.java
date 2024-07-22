package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyMemberMongoRepository extends JpaRepository<FamilyMember, String> {
    List<FamilyMember> findByFamilyId(String familyId);
}
