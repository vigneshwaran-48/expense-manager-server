package com.vapps.expense.common.service;

import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.exception.AppException;

public interface CategoryService {

    CategoryDTO addCategory(String userId, CategoryDTO category) throws AppException;

    CategoryDTO updatedCategory(String userId, String categoryId, CategoryDTO category) throws AppException;

    void deleteCategory(String userId, String categoryId) throws AppException;
}
