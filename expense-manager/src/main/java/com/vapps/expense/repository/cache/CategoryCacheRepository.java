package com.vapps.expense.repository.cache;

import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.model.Category;
import com.vapps.expense.repository.CategoryRepository;
import com.vapps.expense.repository.mongo.CategoryMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryCacheRepository implements CategoryRepository {

    @Autowired
    private CategoryMongoRepository categoryRepository;

    @Override
    @Cacheable(value = "category_createdBy", key = "'category_created_by_' + #createdById + '_' + #type")
    public List<Category> findByCreatedByIdAndType(String createdById, CategoryDTO.CategoryType type) {
        return categoryRepository.findByCreatedByIdAndType(createdById, type);
    }

    @Override
    @CacheEvict(value = "category_createdBy")
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    @CacheEvict(value = "category_createdBy")
    public Category update(Category category) {
        return categoryRepository.save(category);
    }
}
