package com.vapps.expense.repository;

import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findByCreatedByIdAndType(String createdById, CategoryDTO.CategoryType type);

    Optional<Category> findByCreatedByIdAndTypeAndName(String id, CategoryDTO.CategoryType type, String name)
            throws AppException;

    Category save(Category category);

    Category update(Category category);
}
