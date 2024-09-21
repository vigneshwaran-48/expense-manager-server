package com.vapps.expense.repository.cache;

import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.model.FamilyMember;
import com.vapps.expense.repository.FamilyMemberRepository;
import com.vapps.expense.repository.mongo.FamilyMemberMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FamilyMemberCacheRepository implements FamilyMemberRepository {

	@Autowired
	private FamilyMemberMongoRepository familyMemberRepository;

	@Override
	@CacheEvict(value = "familyMember", allEntries = true)
	public FamilyMember save(FamilyMember familyMember) {
		return familyMemberRepository.save(familyMember);
	}

	@Override
	@Cacheable(value = "familyMember", key = "'familyMembers_' + #id")
	public List<FamilyMember> findByFamilyId(String familyId) {
		return familyMemberRepository.findByFamilyId(familyId);
	}

	@Override
	@Cacheable(value = "familyMember", key = "'family_member_' + #memberId")
	public Optional<FamilyMember> findByMemberId(String memberId) {
		return familyMemberRepository.findByMemberId(memberId);
	}

	@Override
	@Cacheable(value = "familyMember", key = "'family_id_' + #familyId + '_member_' + #memberId")
	public Optional<FamilyMember> findByFamilyIdAndMemberId(String familyId, String memberId) {
		return familyMemberRepository.findByFamilyIdAndMemberId(familyId, memberId);
	}

	@Override
	@Cacheable(value = "familyMember", key = "'member_exists_' + #familyId + '_' + #memberId")
	public boolean existsByFamilyIdAndMemberId(String familyId, String memberId) {
		return familyMemberRepository.existsByFamilyIdAndMemberId(familyId, memberId);
	}

	@Override
	@CacheEvict(value = "familyMember", allEntries = true)
	public FamilyMember updateRole(FamilyMember familyMember) {
		return familyMemberRepository.save(familyMember);
	}

	@Override
	@Cacheable(value = "familyMember", key = "'family_' + #familyId + '_role'")
	public List<FamilyMember> findByFamilyIdAndRole(String familyId, FamilyMemberDTO.Role role) {
		return familyMemberRepository.findByFamilyIdAndRole(familyId, role);
	}

	@Override
	@CacheEvict(value = "familyMember", allEntries = true)
	public void deleteByFamilyIdAndMemberId(String familyId, String memberId) {
		familyMemberRepository.deleteByFamilyIdAndMemberId(familyId, memberId);
	}

	@Override
	@CacheEvict(value = "familyMember", allEntries = true)
	public void deleteByFamilyId(String familyId) {
		familyMemberRepository.deleteByFamilyId(familyId);
	}

}
