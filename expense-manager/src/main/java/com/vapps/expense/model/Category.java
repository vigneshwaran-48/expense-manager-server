package com.vapps.expense.model;

import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.dto.CategoryDTO.CategoryType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Category {

    @Id
    private String id;

    private String name;
    private String description;
    private String image;
    private User createdBy;
    private CategoryType type;
    private String ownerId;

    public CategoryDTO toDTO() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(id);
        categoryDTO.setDescription(description);
        categoryDTO.setImage(image);
        categoryDTO.setType(type);
        categoryDTO.setName(name);
        categoryDTO.setOwnerId(ownerId);
        return categoryDTO;
    }

}
