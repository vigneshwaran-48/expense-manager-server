package com.vapps.expense.repository.cache;

import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.model.Family;
import com.vapps.expense.repository.FamilyRepository;
import com.vapps.expense.repository.mongo.FamilyMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FamilyCacheRepository implements FamilyRepository {

    @Autowired
    private FamilyMongoRepository familyRepository;

    @Override
    @Cacheable(value = "family", key = "'family' + #id")
    @CacheEvict(value = "familySearch", allEntries = true)
    public Optional<Family> findById(String id) {
        return familyRepository.findById(id);
    }

    @Override
    public Family save(Family family) {
        return familyRepository.save(family);
    }

    @Override
    @Cacheable(value = "family", key = "'family' + #family.getId()")
    @CacheEvict(value = "familySearch", allEntries = true)
    public Family update(Family family) {
        return familyRepository.save(family);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "familyCreatedBy", allEntries = true), @CacheEvict(value = "family", key = "'family' + #id"),
            @CacheEvict(value = "familySearch", allEntries = true) })
    public void deleteById(String id) {
        familyRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "familyCreatedBy", key = "'created_by_' + #createdById")
    public Optional<Family> findByCreatedById(String createdById) {
        return familyRepository.findByCreatedById(createdById);
    }

    @Override
    @Cacheable(value = "familySearch", key = "#query + '_' + #visibility + '_' + #pageable.getPageNumber()")
    public List<Family> findByIdOrNameContainingIgnoreCaseAndVisibility(String id, String query,
            FamilyDTO.Visibility visibility, Pageable pageable) {
        return familyRepository.findByIdOrNameContainingIgnoreCaseAndVisibility(id, query, visibility, pageable);
    }

    @Override
    @Cacheable(value = "familySearch", key = "#query + #visibility + '_all'")
    public List<Family> findByIdOrNameContainingIgnoreCaseAndVisibility(String id, String query,
            FamilyDTO.Visibility visibility) {
        return familyRepository.findByIdOrNameContainingIgnoreCaseAndVisibility(id, query, visibility);
    }
}
