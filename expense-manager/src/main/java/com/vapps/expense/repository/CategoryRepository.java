package com.vapps.expense.repository;

import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.model.Category;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findById(String id);

    List<Category> findByOwnerIdAndType(String createdById, CategoryDTO.CategoryType type);

    Optional<Category> findByIdAndOwnerId(String id, String ownerId);

    Optional<Category> findByOwnerIdAndTypeAndName(String id, CategoryDTO.CategoryType type, String name)
            throws AppException;

    Category save(Category category);

    Category update(Category category);

    List<Category> findByCreatedByIdOrOwnerId(String createdById, String ownerId);

    @Transactional
    void deleteById(String id);
}
