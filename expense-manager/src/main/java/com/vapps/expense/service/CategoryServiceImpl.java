package com.vapps.expense.service;

import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.CategoryService;
import com.vapps.expense.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${app.default.user.id}")
    private String defaultUserId;

    @Override
    @UserIdValidator(positions = 0)
    public CategoryDTO addCategory(String userId, CategoryDTO category) throws AppException {

    }

    @Override
    public CategoryDTO updatedCategory(String userId, String categoryId, CategoryDTO category) throws AppException {
        return null;
    }

    @Override
    public void deleteCategory(String userId, String categoryId) throws AppException {

    }
}
