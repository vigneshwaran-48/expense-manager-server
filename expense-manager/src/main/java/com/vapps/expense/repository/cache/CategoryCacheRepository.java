package com.vapps.expense.repository.cache;

import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.model.Category;
import com.vapps.expense.repository.CategoryRepository;
import com.vapps.expense.repository.mongo.CategoryMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryCacheRepository implements CategoryRepository {

    @Autowired
    private CategoryMongoRepository categoryRepository;

    @Override
    @Cacheable(value = "category", key = "'category_' + #id")
    public Optional<Category> findById(String id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Cacheable(value = "category_owner_id", key = "'category_owner_id_by_' + #ownerId + '_' + #type")
    public List<Category> findByOwnerIdAndType(String ownerId, CategoryDTO.CategoryType type) {
        return categoryRepository.findByOwnerIdAndType(ownerId, type);
    }

    @Override
    @Cacheable(value = "category_id_owner_id", key = "'category_id_' + #id + '_owner_' + #ownerId")
    public Optional<Category> findByIdAndOwnerId(String id, String ownerId) {
        return categoryRepository.findByIdAndOwnerId(id, ownerId);
    }

    @Override
    @Cacheable(value = "category_owner_id_name", key = "'category_owner_id_by_' + #ownerId + '_' + #type + '_' + #name")
    public Optional<Category> findByOwnerIdAndTypeAndName(String ownerId, CategoryDTO.CategoryType type, String name)
            throws AppException {
        return categoryRepository.findByOwnerIdAndTypeAndName(ownerId, type, name);
    }

    @Override
    @Cacheable(value = "category_created_by_owner", key = "'category_created_by_' + #createdById + '_owner_id_' + #ownerId")
    public List<Category> findByCreatedByIdOrOwnerId(String createdById, String ownerId) {
        return categoryRepository.findByCreatedByIdOrOwnerId(createdById, ownerId);
    }

    /**
     * When a error occurs in the DB level the return value would be null. Because of this we are not using
     *
     * @CachePut with the return value in save() and update() method. It will end up in a NullPointerException
     */

    @Override
    @Caching(evict = {@CacheEvict(value = "category_owner_id", allEntries = true), @CacheEvict(value = "category_owner_id_name", allEntries = true),
            @CacheEvict(value = "category_id_owner_id", allEntries = true), @CacheEvict(value = "category", allEntries = true), @CacheEvict(value = "category_created_by_owner", allEntries = true)})
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "category_owner_id", allEntries = true), @CacheEvict(value = "category_owner_id_name", allEntries = true),
            @CacheEvict(value = "category_id_owner_id", allEntries = true), @CacheEvict(value = "category", allEntries = true), @CacheEvict(value = "category_created_by_owner", allEntries = true)})
    public Category update(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "category_owner_id", allEntries = true), @CacheEvict(value = "category_owner_id_name", allEntries = true),
            @CacheEvict(value = "category_id_owner_id", allEntries = true), @CacheEvict(value = "category", allEntries = true), @CacheEvict(value = "category_created_by_owner", allEntries = true)})
    public void deleteById(String id) {
        categoryRepository.deleteById(id);
    }
}
