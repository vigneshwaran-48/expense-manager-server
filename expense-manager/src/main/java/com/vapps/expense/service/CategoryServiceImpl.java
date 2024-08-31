package com.vapps.expense.service;

import com.vapps.expense.annotation.CategoryIdValidator;
import com.vapps.expense.annotation.UserIdValidator;
import com.vapps.expense.common.dto.CategoryDTO;
import com.vapps.expense.common.dto.FamilyDTO;
import com.vapps.expense.common.dto.FamilyMemberDTO;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.CategoryService;
import com.vapps.expense.common.service.FamilyService;
import com.vapps.expense.common.service.UserService;
import com.vapps.expense.model.Category;
import com.vapps.expense.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    @UserIdValidator(positions = 0)
    public CategoryDTO addCategory(String userId, CategoryDTO category) throws AppException {
        checkCategoryAccessLevel(userId, category);
        checkDuplicateName(userId, category);
        formatCategory(userId, category);
        Category categoryModel = Category.build(category);
        categoryModel = categoryRepository.save(categoryModel);
        if (categoryModel == null) {
            throw new AppException("Error while creating category!");
        }
        return categoryModel.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    @CategoryIdValidator(userIdPosition = 0, positions = 1)
    public CategoryDTO updatedCategory(String userId, String categoryId, CategoryDTO category) throws AppException {
        category.setId(categoryId);
        Category existingCategory = categoryRepository.findById(categoryId).get();

        // Fields that can't be updated
        category.setType(existingCategory.getType());
        category.setOwnerId(existingCategory.getOwnerId());
        category.setCreatedBy(existingCategory.getCreatedBy().toDTO());

        // Fields that can be updated
        if (category.getName() == null) {
            category.setName(existingCategory.getName());
        }
        if (category.getDescription() == null) {
            category.setDescription(existingCategory.getDescription());
        }
        if (category.getImage() == null) {
            category.setImage(existingCategory.getImage());
        }
        checkCategoryAccessLevel(userId, category);
        checkDuplicateName(userId, category);
        Category updatedCategory = categoryRepository.update(Category.build(category));
        if (updatedCategory == null) {
            throw new AppException("Error while updating category!");
        }
        return updatedCategory.toDTO();
    }

    @Override
    @UserIdValidator(positions = 0)
    @CategoryIdValidator(userIdPosition = 0, positions = 1)
    public void deleteCategory(String userId, String categoryId) throws AppException {
        Category category = categoryRepository.findById(categoryId).get();
        if (category.getType() == CategoryDTO.CategoryType.FAMILY) {
            FamilyMemberDTO.Role role = familyService.getUserRoleInFamily(userId, category.getOwnerId());
            if (role != FamilyMemberDTO.Role.LEADER && role != FamilyMemberDTO.Role.MAINTAINER) {
                throw new AppException(HttpStatus.FORBIDDEN.value(),
                        "You are not allowed to delete this family's category");
            }
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public Optional<CategoryDTO> getCategory(String userId, String categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findByIdAndOwnerId(categoryId, userId);
        if (categoryOptional.isPresent()) {
            return Optional.of(categoryOptional.get().toDTO());
        }
        LOGGER.info("Category {} not exists in the user {}'s level", categoryId, userId);
        Optional<FamilyDTO> family = familyService.getUserFamily(userId);
        if (family.isEmpty()) {
            LOGGER.info("User {} not in a family too, So category {} not exists", userId, categoryId);
            return Optional.empty();
        }
        categoryOptional = categoryRepository.findByIdAndOwnerId(categoryId, family.get().getId());
        if (categoryOptional.isPresent()) {
            return Optional.of(categoryOptional.get().toDTO());
        }
        return Optional.empty();
    }

    @Override
    @UserIdValidator(positions = 0)
    public List<CategoryDTO> getAllCategories(String userId) throws AppException {
        Optional<FamilyDTO> family = familyService.getUserFamily(userId);
        String familyId = null;
        if (family.isPresent()) {
            familyId = family.get().getId();
        }
        List<Category> categories = categoryRepository.findByCreatedByIdOrOwnerId(userId, familyId);
        return categories.stream().map(Category::toDTO).toList();
    }

    private void checkCategoryAccessLevel(String userId, CategoryDTO category) throws AppException {
        if (category.getType() == CategoryDTO.CategoryType.FAMILY) {
            if (category.getOwnerId() == null) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(), "ownerId is required for family type category!");
            }
            if (familyService.getFamilyById(userId, category.getOwnerId()).isEmpty()) {
                throw new AppException(HttpStatus.BAD_REQUEST.value(),
                        "Family " + category.getOwnerId() + " not " + "exists!");
            }
            FamilyMemberDTO.Role role = familyService.getUserRoleInFamily(userId, category.getOwnerId());
            if (role != FamilyMemberDTO.Role.LEADER && role != FamilyMemberDTO.Role.MAINTAINER) {
                throw new AppException(HttpStatus.FORBIDDEN.value(),
                        "You are not allowed to perform category operation in this family");
            }
        }
    }

    private void checkDuplicateName(String userId, CategoryDTO category) throws AppException {
        if (categoryRepository.findByOwnerIdAndTypeAndName(category.getOwnerId(), category.getType(),
                category.getName()).isPresent()) {
            throw new AppException(HttpStatus.BAD_REQUEST.value(),
                    "Category with name " + category.getName() + " already exists!");
        }
    }

    private void formatCategory(String userId, CategoryDTO category) throws AppException {
        if (category.getType() == CategoryDTO.CategoryType.PERSONAL) {
            category.setOwnerId(userId);
        }
        category.setCreatedBy(userService.getUser(userId).get());
    }
}
