package com.vapps.expense.repository.cache;

import com.vapps.expense.model.StaticResource;
import com.vapps.expense.repository.StaticResourceRepository;
import com.vapps.expense.repository.mongo.StaticResourceMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StaticResourceCacheRepository implements StaticResourceRepository {

    @Autowired
    private StaticResourceMongoRepository staticResourceRepository;

    @Override
    @Cacheable(value = "staticResource", key = "'resource_' + #id")
    public Optional<StaticResource> findById(String id) {
        return staticResourceRepository.findById(id);
    }

    @Override
    public Optional<StaticResource> findByOwnerIdAndId(String ownerId, String id) {
        return staticResourceRepository.findByOwnerIdAndId(ownerId, id);
    }

    @Override
    @Cacheable(value = "staticResource", key = "'resource_' + #return.getId()")
    public StaticResource save(StaticResource staticResource) {
        return staticResourceRepository.save(staticResource);
    }

    @Override
    @CacheEvict(value = "staticResource", key = "'resource_' + #id")
    public void deleteByIdAndOwnerId(String id, String ownerId) {
        staticResourceRepository.deleteByIdAndOwnerId(id, ownerId);
    }
}