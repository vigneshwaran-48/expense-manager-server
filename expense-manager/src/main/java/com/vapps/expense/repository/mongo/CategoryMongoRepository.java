package com.vapps.expense.repository.mongo;

import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryMongoRepository extends MongoRepository<Category, String> {

    List<Category> findByCreatedByIdAndType(String createdById, CategoryDTO.CategoryType type);

}
