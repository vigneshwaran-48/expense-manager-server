package com.vapps.expense.repository.cache;

import com.vapps.expense.model.FamilyMember;
import com.vapps.expense.repository.FamilyMemberRepository;
import com.vapps.expense.repository.mongo.FamilyMemberMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FamilyMemberCacheRepository implements FamilyMemberRepository {

    @Autowired
    private FamilyMemberMongoRepository familyMemberRepository;

    @Override
    public FamilyMember save(FamilyMember familyMember) {
        return familyMemberRepository.save(familyMember);
    }

    @Override
    @CacheEvict(value = "familyMember", key = "'familyMembers_' + #id")
    public FamilyMember update(FamilyMember familyMember) {
        return familyMemberRepository.save(familyMember);
    }

    @Override
    @Cacheable(value = "familyMember", key = "'familyMembers_' + #id")
    public List<FamilyMember> findByFamilyId(String familyId) {
        return familyMemberRepository.findByFamilyId(familyId);
    }


}
