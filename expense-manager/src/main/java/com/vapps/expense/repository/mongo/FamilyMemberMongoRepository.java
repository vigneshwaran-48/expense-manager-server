package com.vapps.expense.repository.mongo;

import com.vapps.expense.model.FamilyMember;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FamilyMemberMongoRepository extends MongoRepository<FamilyMember, String> {
    List<FamilyMember> findByFamilyId(String familyId);
}
