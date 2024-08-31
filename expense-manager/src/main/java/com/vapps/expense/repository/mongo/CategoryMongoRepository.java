package com.vapps.expense.repository.mongo;

import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryMongoRepository extends MongoRepository<Category, String> {

    List<Category> findByOwnerIdAndType(String createdById, CategoryDTO.CategoryType type);

    Optional<Category> findByOwnerIdAndTypeAndName(String id, CategoryDTO.CategoryType type, String name)
            throws AppException;

    Optional<Category> findByIdAndOwnerId(String id, String ownerId);

    List<Category> findByCreatedByIdOrOwnerId(String createdById, String ownerId);
}
