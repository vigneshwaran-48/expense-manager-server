package com.vapps.expense.repository.cache;

import com.vapps.expense.model.Family;
import com.vapps.expense.repository.FamilyRepository;
import com.vapps.expense.repository.mongo.FamilyMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FamilyCacheRepository implements FamilyRepository {

    @Autowired
    private FamilyMongoRepository familyRepository;

    @Override
    @Cacheable(value = "family", key = "'family' + #id")
    public Optional<Family> findById(String id) {
        return familyRepository.findById(id);
    }

    @Override
    public Family save(Family family) {
        return familyRepository.save(family);
    }

    @Override
    @Cacheable(value = "family", key = "'family' + #family.getId()")
    public Family update(Family family) {
        return familyRepository.save(family);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "familyCreatedBy"), @CacheEvict(value = "family", key = "'family' + #id") })
    public void deleteById(String id) {
        familyRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "familyCreatedBy", key = "'created_by_' + #createdById")
    public Optional<Family> findByCreatedById(String createdById) {
        return familyRepository.findByCreatedById(createdById);
    }
}
